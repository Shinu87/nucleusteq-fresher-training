"""
API routes for registration and login.
"""

from fastapi import APIRouter, Depends, status
from beanie import PydanticObjectId

from backend.constants.api_constants import APIPrefixes, APITags
from backend.exceptions.custom_exceptions import UserNotFoundException
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
from backend.services.auth_service import AuthService, get_auth_service
from backend.services.doctor_profile_service import (
    DoctorProfileService,
    get_doctor_profile_service,
)
from backend.utils.jwt_handler import create_access_token
from backend.config import get_settings


router = APIRouter(prefix=APIPrefixes.AUTH, tags=[APITags.AUTHENTICATION])
settings = get_settings()


def _to_profile_response(user: User) -> UserProfileResponse:
    return UserProfileResponse(
        id=str(user.id),
        full_name=user.full_name,
        email=user.email,
        phone_number=user.phone_number,
        role=user.role,
        gender=user.gender,
        date_of_birth=user.date_of_birth,
        account_status=user.account_status,
        created_at=user.created_at,
    )


@router.post(
    "/register/patient",
    response_model=UserProfileResponse,
    status_code=status.HTTP_201_CREATED,
)
async def register_patient(
    payload: PatientRegisterRequest,
    auth_service: AuthService = Depends(get_auth_service),
):
    user = await auth_service.register_patient(payload)
    return _to_profile_response(user)


@router.post(
    "/register/doctor",
    response_model=DoctorProfileResponse,
    status_code=status.HTTP_201_CREATED,
)
async def register_doctor(
    payload: DoctorRegisterRequest,
    doctor_profile_service: DoctorProfileService = Depends(get_doctor_profile_service),
):
    user, profile = await doctor_profile_service.submit_doctor_application(payload)
    return to_doctor_profile_response(user, profile)


@router.post("/set-password", response_model=UserProfileResponse)
async def set_password(
    payload: SetPasswordRequest,
    auth_service: AuthService = Depends(get_auth_service),
):
    user = await auth_service.set_password(payload.token, payload.new_password)
    return _to_profile_response(user)


@router.post("/login", response_model=TokenResponse)
async def login(
    payload: LoginRequest,
    auth_service: AuthService = Depends(get_auth_service),
):
    user = await auth_service.authenticate_user(payload)
    access_token = create_access_token(user)

    return TokenResponse(
        access_token=access_token,
        token_type="bearer",
        expires_in=settings.jwt_expire_minutes * 60,
        user=_to_profile_response(user),
    )


@router.get("/my-profile", response_model=UserProfileResponse)
async def get_my_profile(
    current_user: CurrentUser = Depends(get_current_user),
):
    user = await User.get(PydanticObjectId(current_user.id))

    if user is None:
        raise UserNotFoundException()

    return _to_profile_response(user)