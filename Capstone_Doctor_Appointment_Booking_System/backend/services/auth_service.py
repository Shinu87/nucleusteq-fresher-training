"""
Business logic for registration. The router just calls these functions,
all the actual decisions (like checking for duplicate emails) happen here.
"""

from fastapi import HTTPException, status

from backend.constants.roles import Role
from backend.models.user import User
from backend.schemas.request.auth_request import DoctorRegisterRequest, PatientRegisterRequest
from backend.utils.security import hash_password


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