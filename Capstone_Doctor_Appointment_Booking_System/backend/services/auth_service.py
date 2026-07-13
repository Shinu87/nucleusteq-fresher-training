"""
Business logic for registration and login.
"""

import logging

from beanie import PydanticObjectId
from fastapi import Depends, HTTPException, status

from backend.constants.approval_status import ApprovalStatus
from backend.constants.roles import Role
from backend.models.user import User
from backend.constants.account_status import AccountStatus
from backend.models.doctor_profile import DoctorProfile
from backend.repositories.doctor_profile_repository import (
    DoctorProfileRepository,
    get_doctor_profile_repository,
)
from backend.repositories.user_repository import UserRepository, get_user_repository
from backend.schemas.request.auth_request import (
    DoctorRegisterRequest,
    LoginRequest,
    PatientRegisterRequest,
)
from backend.utils.security import hash_password, verify_password
from backend.utils.token_utils import hash_setup_token, is_setup_token_expired
from backend.exceptions.custom_exceptions import (
    AccountInactiveException,
    AccountPendingApprovalException,
    AccountRejectedException,
    InvalidCredentialsException,
    InvalidSetupTokenException,
    SetupTokenExpiredException,
)
from backend.exceptions.custom_exceptions import EmailAlreadyRegisteredException, UserNotFoundException
from backend.services.doctor_sync_service import DoctorSyncService, get_doctor_sync_service

logger = logging.getLogger(__name__)


class AuthService:

    def __init__(
            self,
            user_repository: UserRepository,
            doctor_profile_repository: DoctorProfileRepository,
            doctor_sync_service: DoctorSyncService,          # <-- added
        ):
            self._user_repository = user_repository
            self._doctor_profile_repository = doctor_profile_repository
            self._doctor_sync_service = doctor_sync_service  # <-- added
    async def register_patient(self, payload: PatientRegisterRequest) -> User:
        existing_user = await self._user_repository.find_by_email(payload.email)
        if existing_user:
            raise EmailAlreadyRegisteredException()

        new_user = User(
            full_name=payload.full_name,
            email=payload.email,
            password_hash=hash_password(payload.password),
            phone_number=payload.phone_number,
            role=Role.PATIENT,
            gender=payload.gender,
            date_of_birth=payload.date_of_birth,
        )
        await self._user_repository.insert(new_user)
        return new_user

    async def authenticate_user(self, payload: LoginRequest) -> User:
        user = await self._user_repository.find_by_email(payload.email)

        if not user or not verify_password(payload.password, user.password_hash):
            logger.warning("Failed login attempt for email: %s", payload.email)
            raise InvalidCredentialsException()

        # Allow inactive doctors to log in so they can manage their profile and slots.
        # Block inactive accounts for all other roles.
        if user.account_status != AccountStatus.ACTIVE and user.role != Role.DOCTOR:
            logger.warning("Login attempt on inactive account: %s", payload.email)
            raise AccountInactiveException()

        logger.info("User logged in successfully: %s", payload.email)
        return user


    async def _build_inactive_login_exception(self, user: User) -> Exception:
        if user.role == Role.DOCTOR:
            profile = await self._doctor_profile_repository.find_by_user_id(user.id)
            if profile is not None:
                if profile.approval_status == ApprovalStatus.PENDING:
                    return AccountPendingApprovalException()
                if profile.approval_status == ApprovalStatus.REJECTED:
                    return AccountRejectedException()

        return AccountInactiveException()

    async def set_password(self, token: str, new_password: str) -> User:
        token_hash = hash_setup_token(token)
        profile = await self._doctor_profile_repository.find_by_setup_token_hash(token_hash)

        if profile is None:
            raise InvalidSetupTokenException()

        if profile.setup_token_expiry is None or is_setup_token_expired(profile.setup_token_expiry):
            raise SetupTokenExpiredException()

        user = await self._user_repository.get_by_id(profile.user_id)
        if user is None:
            raise UserNotFoundException()

        user.password_hash = hash_password(new_password)
        user.account_status = AccountStatus.ACTIVE
        await self._user_repository.save(user)

        profile.setup_token_hash = None
        profile.setup_token_expiry = None
        await self._doctor_profile_repository.save(profile)

        await self._doctor_sync_service.sync_doctor(user=user, profile=profile, is_active=True)

        logger.info("Doctor completed password setup and is now ACTIVE: %s", user.email)
        return user

    async def get_profile_by_id(self, user_id: PydanticObjectId) -> User:
        user = await self._user_repository.get_by_id(user_id)
        if user is None:
            raise UserNotFoundException()
        return user


def get_auth_service(
    user_repository: UserRepository = Depends(get_user_repository),
    doctor_profile_repository: DoctorProfileRepository = Depends(get_doctor_profile_repository),
    doctor_sync_service: DoctorSyncService = Depends(get_doctor_sync_service),
) -> AuthService:
    return AuthService(
        user_repository,
        doctor_profile_repository,
        doctor_sync_service,
    )