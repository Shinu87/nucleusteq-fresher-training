"""
Business logic for the doctor profile and approval workflow:

    Doctor Registration -> PENDING_APPROVAL -> Admin Approve/Reject
        -> Setup Password Email -> Doctor Creates Password -> ACTIVE
"""

import logging
from datetime import datetime, timezone
from beanie import PydanticObjectId

from fastapi import Depends

from backend.constants.account_status import AccountStatus
from backend.constants.approval_status import ApprovalStatus
from backend.constants.doctor_messages import LINKED_USER_NOT_FOUND
from backend.constants.roles import Role
from backend.models.doctor_profile import DoctorProfile
from backend.models.notification import NotificationType
from backend.models.user import User
from backend.repositories.doctor_profile_repository import (
    DoctorProfileRepository,
    get_doctor_profile_repository,
)
from backend.repositories.user_repository import UserRepository, get_user_repository
from backend.schemas.request.doctor_request import DoctorRegisterRequest
from backend.schemas.request.internal_request import DoctorSyncRequest, SendNotificationRequest
from backend.services.doctor_sync_service import DoctorSyncService, get_doctor_sync_service
from backend.services.notification_service import NotificationService, get_notification_service
from backend.utils.token_utils import (
    generate_setup_token,
    get_setup_token_expiry,
    hash_setup_token,
)
from backend.config import get_settings
from backend.exceptions.custom_exceptions import (
    ApplicationAlreadyReviewedException,
    DoctorApplicationNotFoundException,
    DoctorNotApprovedException,
    DuplicateLicenseException,
    EmailAlreadyRegisteredException,
    UserNotFoundException,
)
settings = get_settings()

logger = logging.getLogger(__name__)


class DoctorProfileService:

    def __init__(
        self,
        user_repository: UserRepository,
        doctor_profile_repository: DoctorProfileRepository,
        doctor_sync_service: DoctorSyncService,
        notification_service: NotificationService,
    ):
        self._user_repository = user_repository
        self._doctor_profile_repository = doctor_profile_repository
        self._doctor_sync_service = doctor_sync_service
        self._notification_service = notification_service

    async def submit_doctor_application(self, payload: DoctorRegisterRequest) -> tuple[User, DoctorProfile]:
        existing_user = await self._user_repository.find_by_email(payload.email)
        if existing_user:
            raise EmailAlreadyRegisteredException()

        existing_license = await self._doctor_profile_repository.find_by_license_number(
            payload.license_number
        )
        if existing_license:
            raise DuplicateLicenseException()

        new_user = User(
            full_name=payload.full_name,
            email=payload.email,
            password_hash=None,  
            phone_number=payload.phone_number,
            role=Role.DOCTOR,
            gender=payload.gender,
            account_status=AccountStatus.INACTIVE,
        )
        await self._user_repository.insert(new_user)

        new_profile = DoctorProfile(
            user_id=new_user.id,
            qualification=payload.qualification,
            specialization=payload.specialization,
            experience_years=payload.experience_years,
            license_number=payload.license_number,
            consultation_fee=payload.consultation_fee,
            clinic_address=payload.clinic_address,
            approval_status=ApprovalStatus.PENDING,
        )
        await self._doctor_profile_repository.insert(new_profile)

        logger.info("New doctor application submitted: %s", new_user.email)
        return new_user, new_profile

    async def list_doctor_applications(self, approval_status: ApprovalStatus | None = None) -> list[dict]:
        if approval_status is not None:
            profiles = await self._doctor_profile_repository.find_by_approval_status(approval_status)
        else:
            profiles = await self._doctor_profile_repository.find_all()

        results = []
        for profile in profiles:
            user = await self._user_repository.get_by_id(profile.user_id)
            if user is not None:
                results.append({"user": user, "profile": profile})
        return results

    async def _get_profile_and_user(self, doctor_profile_id) -> tuple[DoctorProfile, User]:
        profile = await self._doctor_profile_repository.get_by_id(doctor_profile_id)
        if profile is None:
            raise DoctorApplicationNotFoundException()

        user = await self._user_repository.get_by_id(profile.user_id)
        if user is None:
            raise UserNotFoundException(LINKED_USER_NOT_FOUND)

        return profile, user

    async def approve_doctor(self, doctor_profile_id, admin_id) -> tuple[User, DoctorProfile]:
        profile, user = await self._get_profile_and_user(doctor_profile_id)

        if profile.approval_status != ApprovalStatus.PENDING:
            raise ApplicationAlreadyReviewedException(profile.approval_status.value.lower())

        raw_token = generate_setup_token()
        profile.approval_status = ApprovalStatus.APPROVED
        profile.reviewed_by = admin_id
        profile.reviewed_at = datetime.now(timezone.utc)
        profile.setup_token_hash = hash_setup_token(raw_token)
        profile.setup_token_expiry = get_setup_token_expiry()
        await self._doctor_profile_repository.save(profile)

        setup_link = f"{settings.FRONTEND_URL}/set-password/{raw_token}"
        await self._notification_service.send_notification(SendNotificationRequest(
            recipient_email=user.email,
            type=NotificationType.SETUP_PASSWORD,
            payload={"setup_link": setup_link},
        ))
        await self._doctor_sync_service.upsert_doctor(DoctorSyncRequest(
            doctor_id=str(user.id),
            full_name=user.full_name,
            specialization=profile.specialization,
            qualification=profile.qualification,
            experience_years=profile.experience_years,
            consultation_fee=profile.consultation_fee,
            clinic_address=profile.clinic_address,
            is_active=user.account_status == AccountStatus.ACTIVE,
        ))
        logger.info("Doctor application approved: %s", user.email)
        return user, profile

    async def reject_doctor(self, doctor_profile_id, admin_id, reason: str | None) -> tuple[User, DoctorProfile]:
        profile, user = await self._get_profile_and_user(doctor_profile_id)

        if profile.approval_status != ApprovalStatus.PENDING:
            raise ApplicationAlreadyReviewedException(profile.approval_status.value.lower())

        profile.approval_status = ApprovalStatus.REJECTED
        profile.reviewed_by = admin_id
        profile.reviewed_at = datetime.now(timezone.utc)
        await self._doctor_profile_repository.save(profile)

        if user.account_status != AccountStatus.INACTIVE:
            user.account_status = AccountStatus.INACTIVE
            await self._user_repository.save(user)

        logger.info("Doctor application rejected: %s (reason: %s)", user.email, reason)
        return user, profile


    async def set_own_account_status(
        self, doctor_user_id: PydanticObjectId, new_status: AccountStatus
    ) -> tuple[User, DoctorProfile]:

        profile = await self._doctor_profile_repository.find_by_user_id(doctor_user_id)
        if profile is None:
            raise DoctorApplicationNotFoundException()

        if profile.approval_status != ApprovalStatus.APPROVED:
            raise DoctorNotApprovedException()

        user = await self._user_repository.get_by_id(doctor_user_id)
        if user is None:
            raise UserNotFoundException()

        user.account_status = new_status
        await self._user_repository.save(user)

        await self._doctor_sync_service.sync_doctor(
            user=user,
            profile=profile,
            is_active=new_status == AccountStatus.ACTIVE,
        )

        logger.info(
            "Doctor %s switched their own account_status to %s", user.email, new_status.value
        )
        return user, profile



def get_doctor_profile_service(
    user_repository: UserRepository = Depends(get_user_repository),
    doctor_profile_repository: DoctorProfileRepository = Depends(get_doctor_profile_repository),
    doctor_sync_service: DoctorSyncService = Depends(get_doctor_sync_service),
    notification_service: NotificationService = Depends(get_notification_service),
) -> DoctorProfileService:
    return DoctorProfileService(
        user_repository,
        doctor_profile_repository,
        doctor_sync_service,
        notification_service,
    )
