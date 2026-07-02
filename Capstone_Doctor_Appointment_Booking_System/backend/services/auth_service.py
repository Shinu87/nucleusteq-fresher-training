"""
Business logic for registration and login.
"""

import logging

from fastapi import HTTPException, status

from backend.constants.roles import Role
from backend.models.user import User
from backend.constants.account_status import AccountStatus
from backend.models.doctor_profile import DoctorProfile
from backend.schemas.request.auth_request import (
    DoctorRegisterRequest,
    LoginRequest,
    PatientRegisterRequest,
)
from backend.utils.security import hash_password, verify_password
from backend.utils.token_utils import hash_setup_token, is_setup_token_expired

logger = logging.getLogger(__name__)

async def register_patient(payload: PatientRegisterRequest) -> User:
    # here we are checking if the email is already taken before creating the account
    existing_user = await User.find_one(User.email == payload.email)
    if existing_user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Email is already registered",
        )

    # we hash the password here so the plain password never gets saved
    new_user = User(
        full_name=payload.full_name,
        email=payload.email,
        password_hash=hash_password(payload.password),
        phone_number=payload.phone_number,
        role=Role.PATIENT,
        gender=payload.gender,
        date_of_birth=payload.date_of_birth,
    )
    await new_user.insert()
    return new_user


async def authenticate_user(payload: LoginRequest) -> User:
    # Checks an email + password against the database and returns the matching user if everything is correct.
    user = await User.find_one(User.email == payload.email)

    if not user or not verify_password(payload.password, user.password_hash):
        logger.warning("Failed login attempt for email: %s", payload.email)
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid email or password",
        )

    if not user.is_active:
        logger.warning("Login attempt on inactive account: %s", payload.email)
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="This account is not active. Please contact support.",
        )

    logger.info("User logged in successfully: %s", payload.email)
    return user

async def set_password(token: str, new_password: str) -> User:
    """
    Doctor approval workflow: a doctor clicks
    the link from their email and sets their password here. This is what
    actually flips their account_status to ACTIVE - simply being approved
    is not enough to log in.
    """
    token_hash = hash_setup_token(token)
    profile = await DoctorProfile.find_one(DoctorProfile.setup_token_hash == token_hash)

    if profile is None:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="This setup link is invalid or has already been used",
        )

    if profile.setup_token_expiry is None or is_setup_token_expired(profile.setup_token_expiry):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="This setup link has expired. Please ask an admin to re-approve your application.",
        )

    user = await User.get(profile.user_id)
    if user is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User not found")

    user.password_hash = hash_password(new_password)
    user.account_status = AccountStatus.ACTIVE
    await user.save()

    # the token is single-use - clear it so it can never be reused
    profile.setup_token_hash = None
    profile.setup_token_expiry = None
    await profile.save()

    logger.info("Doctor completed password setup and is now ACTIVE: %s", user.email)
    return user