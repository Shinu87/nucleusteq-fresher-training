"""
Business logic for registration and login. The router just calls these
functions, all the actual decisions (like checking for duplicate emails,
or whether a password is correct) happen here.
"""

import logging

from fastapi import HTTPException, status

from backend.constants.roles import Role
from backend.models.user import User
from backend.schemas.request.auth_request import (
    DoctorRegisterRequest,
    LoginRequest,
    PatientRegisterRequest,
)
from backend.utils.security import hash_password, verify_password

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


async def register_doctor(payload: DoctorRegisterRequest) -> User:
    # same duplicate email check as the patient flow
    existing_user = await User.find_one(User.email == payload.email)
    if existing_user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Email is already registered",
        )

    new_user = User(
        full_name=payload.full_name,
        email=payload.email,
        password_hash=hash_password(payload.password),
        phone_number=payload.phone_number,
        role=Role.DOCTOR,
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