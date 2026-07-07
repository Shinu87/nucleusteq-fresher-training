"""
Business logic for registration and login.
"""

import logging

from beanie import PydanticObjectId
from fastapi import Depends, HTTPException, status

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
from backend.exceptions.auth_exception import (
    AccountInactiveException,
    InvalidCredentialsException,
    InvalidSetupTokenException,
    SetupTokenExpiredException,
)
from backend.exceptions.user_exception import EmailAlreadyRegisteredException, UserNotFoundException

logger = logging.getLogger(__name__)


class AuthService:
    """Business logic for registration, login, and the password-setup flow."""

    def __init__(self, user_repository: UserRepository, doctor_profile_repository: DoctorProfileRepository):
        self._user_repository = user_repository
        self._doctor_profile_repository = doctor_profile_repository

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

        if not user.is_active:
            logger.warning("Login attempt on inactive account: %s", payload.email)
            raise AccountInactiveException()

        logger.info("User logged in successfully: %s", payload.email)
        return user

    async def set_password(self, token: str, new_password: str) -> User:
        """
        Doctor approval workflow: a doctor clicks
        the link from their email and sets their password here. This is what
        actually flips their account_status to ACTIVE - simply being approved
        is not enough to log in.
        """
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

        # the token is single-use - clear it so it can never be reused
        profile.setup_token_hash = None
        profile.setup_token_expiry = None
        await self._doctor_profile_repository.save(profile)

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
) -> AuthService:
    """FastAPI dependency provider for AuthService."""
    return AuthService(user_repository, doctor_profile_repository)