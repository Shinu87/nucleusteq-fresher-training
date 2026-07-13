"""
Routes for a doctor managing their own account and requesting
emergency leave. Admin-side review of leave requests lives in
admin_router.py alongside the rest of the admin surface.
"""

from beanie import PydanticObjectId
from fastapi import APIRouter, Depends, status

from backend.constants.api_constants import APIPrefixes, APITags
from backend.constants.roles import Role
from backend.middleware.auth import CurrentUser, require_role
from backend.schemas.request.doctor_request import UpdateAccountStatusRequest
from backend.schemas.request.leave_request_request import LeaveRequestCreate
from backend.schemas.response.doctor_response import (
    DoctorProfileResponse,
    to_doctor_profile_response,
)
from backend.schemas.response.leave_request_response import LeaveRequestResponse
from backend.services.doctor_profile_service import (
    DoctorProfileService,
    get_doctor_profile_service,
)
from backend.services.leave_request_service import (
    LeaveRequestService,
    get_leave_request_service,
)

router = APIRouter(prefix=APIPrefixes.DOCTOR_SELF_SERVICE, tags=[APITags.DOCTOR_SELF_SERVICE])


@router.patch("/account-status", response_model=DoctorProfileResponse)
async def update_own_account_status(
    payload: UpdateAccountStatusRequest,
    current_user: CurrentUser = Depends(require_role(Role.DOCTOR)),
    doctor_profile_service: DoctorProfileService = Depends(get_doctor_profile_service),
):
    """
    Lets the authenticated doctor switch their own account between
    ACTIVE and INACTIVE. This is the only normal way a doctor's
    account_status changes after approval (see AdminService for the
    exceptional admin override).
    """
    user, profile = await doctor_profile_service.set_own_account_status(
        PydanticObjectId(current_user.id), payload.account_status
    )
    return to_doctor_profile_response(user, profile)


@router.post(
    "/leave-request",
    response_model=LeaveRequestResponse,
    status_code=status.HTTP_201_CREATED,
)
async def request_leave(
    payload: LeaveRequestCreate,
    current_user: CurrentUser = Depends(require_role(Role.DOCTOR)),
    leave_request_service: LeaveRequestService = Depends(get_leave_request_service),
):
    leave_request = await leave_request_service.request_leave(
        PydanticObjectId(current_user.id), payload
    )
    return await leave_request_service.to_response(leave_request)


@router.get("/leave-request", response_model=list[LeaveRequestResponse])
async def list_my_leave_requests(
    current_user: CurrentUser = Depends(require_role(Role.DOCTOR)),
    leave_request_service: LeaveRequestService = Depends(get_leave_request_service),
):
    """Lets a doctor view their own leave requests, including any rejection reason."""
    requests = await leave_request_service.list_my_requests(PydanticObjectId(current_user.id))
    return await leave_request_service.to_response_list(requests)