"""
Admin-only routes for reviewing doctor applications.

Every route here is protected with require_role(Role.ADMIN).
"""

from typing import Optional


from beanie import PydanticObjectId
from fastapi import APIRouter, Depends, Query

from backend.constants.approval_status import ApprovalStatus
from backend.constants.roles import Role
from backend.middleware.auth import CurrentUser, require_role
from backend.schemas.request.doctor_request import RejectDoctorRequest
from backend.schemas.response.doctor_response import (
    DoctorProfileResponse,
    to_doctor_profile_response,
)
from backend.services import doctor_profile_service

router = APIRouter(prefix="/admin", tags=["Admin - Doctor Approval"])


@router.get("/doctors", response_model=list[DoctorProfileResponse])
async def list_doctor_applications(
    approval_status: Optional[ApprovalStatus] = Query(default=None),
    current_user: CurrentUser = Depends(require_role(Role.ADMIN)),
):
    """
    Lists doctor applications
    """
    results = await doctor_profile_service.list_doctor_applications(approval_status)
    return [to_doctor_profile_response(item["user"], item["profile"]) for item in results]


@router.post("/doctors/{doctor_profile_id}/approve", response_model=DoctorProfileResponse)
async def approve_doctor(
    doctor_profile_id: PydanticObjectId,
    current_user: CurrentUser = Depends(require_role(Role.ADMIN)),
):
    """
    Approves a pending doctor application. This generates a setup token,
    triggers the setup-password email (via Notification Service), and
    syncs the doctor into Appointment Service's searchable list.
    """
    user, profile = await doctor_profile_service.approve_doctor(
        doctor_profile_id, PydanticObjectId(current_user.id)
    )
    return to_doctor_profile_response(user, profile)


@router.post("/doctors/{doctor_profile_id}/reject", response_model=DoctorProfileResponse)
async def reject_doctor(
    doctor_profile_id: PydanticObjectId,
    payload: RejectDoctorRequest,
    current_user: CurrentUser = Depends(require_role(Role.ADMIN)),
):
    """Rejects a pending doctor application. The doctor can never log in afterwards."""
    user, profile = await doctor_profile_service.reject_doctor(
        doctor_profile_id, PydanticObjectId(current_user.id), payload.reason
    )
    return to_doctor_profile_response(user, profile)