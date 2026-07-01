"""
Routes for patients booking and viewing their own appointments.
"""

from typing import Optional

from beanie import PydanticObjectId
from fastapi import APIRouter, Depends, Query

from appointment_service.clients.user_client import get_patient_info
from appointment_service.constants.appointment_status import AppointmentStatus
from appointment_service.constants.roles import Role
from appointment_service.middleware.auth import CurrentUser, require_role
from appointment_service.schemas.request.booking_request import BookAppointmentRequest
from appointment_service.schemas.response.appointment_response import AppointmentResponse
from appointment_service.services import booking_service

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
    patient_info = await get_patient_info(current_user.id)

    appointment = await booking_service.book_appointment(
        current_user=current_user,
        patient_name=patient_info["full_name"],
        patient_phone=patient_info["phone_number"],
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
    appointments = await booking_service.get_my_appointments(
        patient_id=PydanticObjectId(current_user.id),
        status_filter=appointment_status,
    )
    return [_to_response(appt) for appt in appointments]