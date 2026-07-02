"""
API routes for registration and login. 
These endpoints are public, no login is
needed to call them since this is how a new account gets created.
"""

from fastapi import APIRouter, Depends, HTTPException, status
from beanie import PydanticObjectId

from fastapi import APIRouter, Depends, HTTPException, status
from beanie import PydanticObjectId

from backend.middleware.auth import CurrentUser, get_current_user
from backend.models.user import User
from backend.schemas.request.auth_request import (
    LoginRequest,
    PatientRegisterRequest,
    SetPasswordRequest,
)
from backend.schemas.request.doctor_request import DoctorRegisterRequest
from backend.schemas.response.auth_response import TokenResponse, UserProfileResponse
from backend.schemas.response.doctor_response import (
    DoctorProfileResponse,
    to_doctor_profile_response,
)
from backend.services import auth_service, doctor_profile_service
from backend.utils.jwt_handler import create_access_token
from backend.config import get_settings

router = APIRouter(prefix="/auth", tags=["Authentication"])
settings = get_settings()

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
    response_model=DoctorProfileResponse,
    status_code=status.HTTP_201_CREATED,
)
async def register_doctor(payload: DoctorRegisterRequest):
    user, profile = await doctor_profile_service.submit_doctor_application(payload)
    return to_doctor_profile_response(user, profile)


@router.post("/set-password", response_model=UserProfileResponse)
async def set_password(payload: SetPasswordRequest):
    user = await auth_service.set_password(payload.token, payload.new_password)
    return _to_profile_response(user)


@router.post("/login", response_model=TokenResponse)
async def login(payload: LoginRequest):
    """
    Logs a user in and returns a JWT it will attach to every future
    request as: Authorization: Bearer <access_token>
    """
    user = await auth_service.authenticate_user(payload)
    access_token = create_access_token(user)

    return TokenResponse(
        access_token=access_token,
        token_type="bearer",
        expires_in=settings.jwt_expire_minutes * 60,
        user=_to_profile_response(user),
    )

@router.get("/me", response_model=UserProfileResponse)
async def get_my_profile(current_user: CurrentUser = Depends(get_current_user)):
    """
    Returns the profile of whoever's token was sent in the Authorization header.
    """
    user = await User.get(PydanticObjectId(current_user.id))
    if user is None:
        # this would only happen if the user's account was deleted after
        # their token was issued
        raise HTTPException(status_code=404, detail="User not found")

    return _to_profile_response(user)