"""
Routes for patients booking and viewing their own appointments, plus a
doctors view of and lifecycle actions on those same appointments.
"""

from typing import Optional

from beanie import PydanticObjectId
from fastapi import APIRouter, Depends, Query

from backend.constants.api_constants import APIPrefixes, APITags
from backend.constants.appointment_status import AppointmentStatus
from backend.constants.roles import Role
from backend.middleware.auth import CurrentUser, require_role
from backend.schemas.request.booking_request import BookAppointmentRequest
from backend.schemas.response.appointment_response import AppointmentResponse
from backend.services import booking_service

router = APIRouter(prefix=APIPrefixes.APPOINTMENTS, tags=[APITags.APPOINTMENTS])


def _to_response(appointment) -> AppointmentResponse:
    return AppointmentResponse(
        id=str(appointment.id),
        patient_id=str(appointment.patient_id),
        patient_name=appointment.patient_name,
        doctor_id=str(appointment.doctor_id),
        doctor_name=appointment.doctor_name,
        slot_id=str(appointment.slot_id),
        appointment_date=appointment.appointment_date,
        start_time=appointment.start_time,
        end_time=appointment.end_time,
        status=appointment.status,
        payment_status=appointment.payment_status,
        consultation_fee=appointment.consultation_fee,
        booked_at=appointment.booked_at,
        cancelled_at=appointment.cancelled_at,
        completed_at=appointment.completed_at,
    )


@router.post("", response_model=AppointmentResponse)
async def book_appointment(
    payload: BookAppointmentRequest,
    current_user: CurrentUser = Depends(require_role(Role.PATIENT)),
):
    """
    Books an available slot for the logged-in patient.
    Patient name and phone are fetched inside the service directly.
    """
    appointment = await booking_service.book_appointment(
        current_user=current_user,
        slot_id=PydanticObjectId(payload.slot_id),
    )
    return _to_response(appointment)


@router.get("/me", response_model=list[AppointmentResponse])
async def get_my_appointments(
    appointment_status: Optional[AppointmentStatus] = Query(
        default=None, description="Filter by status: BOOKED, CANCELLED, COMPLETED"
    ),
    current_user: CurrentUser = Depends(require_role(Role.PATIENT)),
):
    """Returns all appointments for the logged-in patient, newest first."""
    appointments = await booking_service.get_my_appointments(
        patient_id=PydanticObjectId(current_user.id),
        status_filter=appointment_status,
    )
    return [_to_response(appt) for appt in appointments]


@router.patch("/{appointment_id}/cancel", response_model=AppointmentResponse)
async def cancel_appointment(
    appointment_id: PydanticObjectId,
    current_user: CurrentUser = Depends(require_role(Role.PATIENT)),
):
    """
    Cancels the logged-in patient's own BOOKED appointment. Only
    allowed at least 2 hours before the scheduled slot.
    """
    appointment = await booking_service.cancel_appointment(
        patient_id=PydanticObjectId(current_user.id),
        appointment_id=appointment_id,
    )
    return _to_response(appointment)


@router.get("/doctor/me", response_model=list[AppointmentResponse])
async def get_doctor_appointments(
    appointment_status: Optional[AppointmentStatus] = Query(
        default=None, description="Filter by status: BOOKED, CANCELLED, COMPLETED, NO_SHOW"
    ),
    sort: str = Query(
        default="asc", pattern="^(asc|desc)$", description="Sort by date/time: asc or desc"
    ),
    current_user: CurrentUser = Depends(require_role(Role.DOCTOR)),
):
    """
    Returns the logged-in doctor's own appointments, sortable by
    appointment date/time.
    """
    appointments = await booking_service.get_doctor_appointments(
        doctor_id=PydanticObjectId(current_user.id),
        status_filter=appointment_status,
        sort_order=sort,
    )
    return [_to_response(appt) for appt in appointments]


@router.patch("/{appointment_id}/complete", response_model=AppointmentResponse)
async def complete_appointment(
    appointment_id: PydanticObjectId,
    current_user: CurrentUser = Depends(require_role(Role.DOCTOR)),
):
    """
    Marks the doctor's own BOOKED appointment as COMPLETED.
    Blocked until the scheduled appointment time has passed.
    """
    appointment = await booking_service.complete_appointment(
        doctor_id=PydanticObjectId(current_user.id),
        appointment_id=appointment_id,
    )
    return _to_response(appointment)


@router.patch("/{appointment_id}/no-show", response_model=AppointmentResponse)
async def mark_appointment_no_show(
    appointment_id: PydanticObjectId,
    current_user: CurrentUser = Depends(require_role(Role.DOCTOR)),
):
    """
    Marks the doctor's own BOOKED appointment as NO_SHOW.
    """
    appointment = await booking_service.mark_appointment_no_show(
        doctor_id=PydanticObjectId(current_user.id),
        appointment_id=appointment_id,
    )
    return _to_response(appointment)