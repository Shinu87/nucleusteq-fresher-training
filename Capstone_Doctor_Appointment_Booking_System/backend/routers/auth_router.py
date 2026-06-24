"""
API routes for registration. These endpoints are public, no login is
needed to call them since this is how a new account gets created.
"""

from fastapi import APIRouter, status

from backend.models.user import User
from backend.schemas.request.auth_request import DoctorRegisterRequest, PatientRegisterRequest
from backend.schemas.response.auth_response import UserProfileResponse
from backend.services import auth_service

router = APIRouter(prefix="/auth", tags=["Authentication"])


def _to_profile_response(user: User) -> UserProfileResponse:
    # this just maps our Mongo document into the shape we want to return from the API
    return UserProfileResponse(
        id=str(user.id),
        full_name=user.full_name,
        email=user.email,
        phone_number=user.phone_number,
        role=user.role,
        gender=user.gender,
        date_of_birth=user.date_of_birth,
        is_active=user.is_active,
        created_at=user.created_at,
    )


@router.post(
    "/register/patient",
    response_model=UserProfileResponse,
    status_code=status.HTTP_201_CREATED,
)
async def register_patient(payload: PatientRegisterRequest):
    # creates a new patient account
    user = await auth_service.register_patient(payload)
    return _to_profile_response(user)


@router.post(
    "/register/doctor",
    response_model=UserProfileResponse,
    status_code=status.HTTP_201_CREATED,
)
async def register_doctor(payload: DoctorRegisterRequest):
    # creates a new doctor account
    user = await auth_service.register_doctor(payload)
    return _to_profile_response(user)