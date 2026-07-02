"""
Routes for patients booking and viewing their own appointments.
"""

from typing import Optional

from beanie import PydanticObjectId
from fastapi import APIRouter, Depends, Query

from backend.constants.appointment_status import AppointmentStatus
from backend.constants.roles import Role
from backend.middleware.auth import CurrentUser, require_role
from backend.schemas.request.booking_request import BookAppointmentRequest
from backend.schemas.response.appointment_response import AppointmentResponse
from backend.services import booking_service

router = APIRouter(prefix="/appointments", tags=["Appointments"])


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
