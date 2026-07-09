"""
Admin-only routes for reviewing doctor applications.
"""

from typing import Optional



from beanie import PydanticObjectId
from fastapi import APIRouter, Depends, Query

from backend.constants.account_status import AccountStatus
from backend.constants.api_constants import APIPrefixes, APITags
from backend.constants.approval_status import ApprovalStatus
from backend.constants.roles import Role
from backend.middleware.auth import CurrentUser, require_role
from backend.schemas.request.doctor_request import RejectDoctorRequest
from backend.schemas.response.admin_response import (
    AdminStatsResponse,
    PatientResponse,
    to_patient_response,
)
from backend.schemas.response.doctor_response import (
    DoctorProfileResponse,
    to_doctor_profile_response,
)
from backend.services.doctor_profile_service import (
    DoctorProfileService,
    get_doctor_profile_service,
)
from backend.services.admin_service import (
    AdminService,
    get_admin_service,
)
router = APIRouter(prefix=APIPrefixes.ADMIN, tags=[APITags.ADMIN_DOCTOR_APPROVAL])

@router.get("/doctors", response_model=list[DoctorProfileResponse])
async def list_doctor_applications(
    approval_status: Optional[ApprovalStatus] = Query(default=None),
    current_user: CurrentUser = Depends(require_role(Role.ADMIN)),
    doctor_profile_service: DoctorProfileService = Depends(get_doctor_profile_service),
):
    results = await doctor_profile_service.list_doctor_applications(approval_status)
    return [to_doctor_profile_response(item["user"], item["profile"]) for item in results]


@router.post("/doctors/{doctor_profile_id}/approve", response_model=DoctorProfileResponse)
async def approve_doctor(
    doctor_profile_id: PydanticObjectId,
    current_user: CurrentUser = Depends(require_role(Role.ADMIN)),
    doctor_profile_service: DoctorProfileService = Depends(get_doctor_profile_service),
):
    user, profile = await doctor_profile_service.approve_doctor(
        doctor_profile_id, PydanticObjectId(current_user.id)
    )
    return to_doctor_profile_response(user, profile)


@router.post("/doctors/{doctor_profile_id}/reject", response_model=DoctorProfileResponse)
async def reject_doctor(
    doctor_profile_id: PydanticObjectId,
    payload: RejectDoctorRequest,
    current_user: CurrentUser = Depends(require_role(Role.ADMIN)),
    doctor_profile_service: DoctorProfileService = Depends(get_doctor_profile_service),
):
    user, profile = await doctor_profile_service.reject_doctor(
        doctor_profile_id, PydanticObjectId(current_user.id), payload.reason
    )
    return to_doctor_profile_response(user, profile)

@router.get("/doctors/{doctor_profile_id}", response_model=DoctorProfileResponse)
async def get_doctor(
    doctor_profile_id: PydanticObjectId,
    current_user: CurrentUser = Depends(require_role(Role.ADMIN)),
    doctor_profile_service: DoctorProfileService = Depends(get_doctor_profile_service),
):
    profile, user = await doctor_profile_service._get_profile_and_user(doctor_profile_id)
    return to_doctor_profile_response(user, profile)


@router.patch("/doctors/{doctor_profile_id}/activate", response_model=DoctorProfileResponse)
async def activate_doctor(
    doctor_profile_id: PydanticObjectId,
    current_user: CurrentUser = Depends(require_role(Role.ADMIN)),
    admin_service: AdminService = Depends(get_admin_service),
):
    user, profile = await admin_service.activate_doctor(
        doctor_profile_id, PydanticObjectId(current_user.id)
    )
    return to_doctor_profile_response(user, profile)


@router.patch("/doctors/{doctor_profile_id}/deactivate", response_model=DoctorProfileResponse)
async def deactivate_doctor(
    doctor_profile_id: PydanticObjectId,
    current_user: CurrentUser = Depends(require_role(Role.ADMIN)),
    admin_service: AdminService = Depends(get_admin_service),
):
    user, profile = await admin_service.deactivate_doctor(
        doctor_profile_id, PydanticObjectId(current_user.id)
    )
    return to_doctor_profile_response(user, profile)


@router.get("/patients", response_model=list[PatientResponse])
async def list_patients(
    account_status: Optional[AccountStatus] = Query(
        default=None, description="Filter by account status"
    ),
    current_user: CurrentUser = Depends(require_role(Role.ADMIN)),
    admin_service: AdminService = Depends(get_admin_service),
):
    patients = await admin_service.list_patients(account_status)
    return [to_patient_response(patient) for patient in patients]


@router.get("/stats", response_model=AdminStatsResponse)
async def get_platform_stats(
    current_user: CurrentUser = Depends(require_role(Role.ADMIN)),
    admin_service: AdminService = Depends(get_admin_service),
):
    return await admin_service.get_platform_stats()