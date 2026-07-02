"""
Business logic for the doctor profile and approval workflow:

    Doctor Registration -> PENDING_APPROVAL -> Admin Approve/Reject
        -> Setup Password Email -> Doctor Creates Password -> ACTIVE
"""

import logging
from datetime import datetime, timezone

from fastapi import HTTPException, status

from backend.schemas.request.internal_request import DoctorSyncRequest
from backend.constants.account_status import AccountStatus
from backend.constants.approval_status import ApprovalStatus
from backend.constants.roles import Role
from backend.models.doctor_profile import DoctorProfile
from backend.models.user import User
from backend.schemas.request.doctor_request import DoctorRegisterRequest
from backend.schemas.request.internal_request import SendNotificationRequest
from backend.services.doctor_sync_service import upsert_doctor
from backend.services.notification_service import send_notification
from backend.models.notification import NotificationType
from backend.utils.token_utils import (
    generate_setup_token,
    get_setup_token_expiry,
    hash_setup_token,
)
from backend.config import get_settings

settings = get_settings()

logger = logging.getLogger(__name__)


async def submit_doctor_application(payload: DoctorRegisterRequest) -> tuple[User, DoctorProfile]:
    """
    Doctor application submission.
    """
    existing_user = await User.find_one(User.email == payload.email)
    if existing_user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Email is already registered",
        )

    existing_license = await DoctorProfile.find_one(
        DoctorProfile.license_number == payload.license_number
    )
    if existing_license:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="This license number is already registered",
        )

    new_user = User(
        full_name=payload.full_name,
        email=payload.email,
        password_hash=None,  # no password yet - set after approval
        phone_number=payload.phone_number,
        role=Role.DOCTOR,
        account_status=AccountStatus.PENDING_APPROVAL,
    )
    await new_user.insert()

    new_profile = DoctorProfile(
        user_id=new_user.id,
        qualification=payload.qualification,
        specialization=payload.specialization,
        experience_years=payload.experience_years,
        license_number=payload.license_number,
        consultation_fee=payload.consultation_fee,
        clinic_address=payload.clinic_address,
        approval_status=ApprovalStatus.PENDING_APPROVAL,
    )
    await new_profile.insert()

    logger.info("New doctor application submitted: %s", new_user.email)
    return new_user, new_profile


async def list_doctor_applications(approval_status: ApprovalStatus | None = None) -> list[dict]:
    """
    Returns doctor applications for the admin review screen
    """
    query = DoctorProfile.find_all()
    if approval_status is not None:
        query = DoctorProfile.find(DoctorProfile.approval_status == approval_status)

    profiles = await query.to_list()

    results = []
    for profile in profiles:
        user = await User.get(profile.user_id)
        if user is not None:
            results.append({"user": user, "profile": profile})
    return results


async def _get_profile_and_user(doctor_profile_id) -> tuple[DoctorProfile, User]:
    profile = await DoctorProfile.get(doctor_profile_id)
    if profile is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Doctor application not found")

    user = await User.get(profile.user_id)
    if user is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Linked user account not found")

    return profile, user


async def approve_doctor(doctor_profile_id, admin_id) -> tuple[User, DoctorProfile]:
    """
    Approval step for a doctor application.
    """
    profile, user = await _get_profile_and_user(doctor_profile_id)

    if profile.approval_status != ApprovalStatus.PENDING_APPROVAL:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"This application has already been {profile.approval_status.value.lower()}",
        )

    raw_token = generate_setup_token()
    profile.approval_status = ApprovalStatus.APPROVED
    profile.reviewed_by = admin_id
    profile.reviewed_at = datetime.now(timezone.utc)
    profile.setup_token_hash = hash_setup_token(raw_token)
    profile.setup_token_expiry = get_setup_token_expiry()
    await profile.save()

    setup_link = f"{settings.FRONTEND_URL}/set-password/{raw_token}"
    await send_notification(SendNotificationRequest(
        recipient_email=user.email,
        type=NotificationType.SETUP_PASSWORD,
        payload={"setup_link": setup_link},
    ))
    await upsert_doctor(DoctorSyncRequest(
        doctor_id=str(user.id),
        full_name=user.full_name,
        specialization=profile.specialization,
        qualification=profile.qualification,
        experience_years=profile.experience_years,
        consultation_fee=profile.consultation_fee,
        clinic_address=profile.clinic_address,
        is_active=user.is_active,
    ))
    logger.info("Doctor application approved: %s", user.email)
    return user, profile


async def reject_doctor(doctor_profile_id, admin_id, reason: str | None) -> tuple[User, DoctorProfile]:
    """
    Reject step for a doctor application.
    """
    profile, user = await _get_profile_and_user(doctor_profile_id)

    if profile.approval_status != ApprovalStatus.PENDING_APPROVAL:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"This application has already been {profile.approval_status.value.lower()}",
        )

    profile.approval_status = ApprovalStatus.REJECTED
    profile.reviewed_by = admin_id
    profile.reviewed_at = datetime.now(timezone.utc)
    await profile.save()

    user.account_status = AccountStatus.REJECTED
    await user.save()

    logger.info("Doctor application rejected: %s (reason: %s)", user.email, reason)
    return user, profile