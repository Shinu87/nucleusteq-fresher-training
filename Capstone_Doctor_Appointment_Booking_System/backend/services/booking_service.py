"""
Business logic for appointment booking.

To prevent double-booking, we first atomically update the slot from
AVAILABLE to BOOKED. If the slot is already booked, we return 409 Conflict.

After that, we create the appointment. A unique index on slot_id acts as
an extra safety check and raises 409 if a duplicate booking is attempted.
"""

import logging
from datetime import date

from beanie import PydanticObjectId
from fastapi import HTTPException, status
from pymongo.errors import DuplicateKeyError

from backend.constants.appointment_status import AppointmentStatus, PaymentStatus
from backend.constants.slot_status import SlotStatus
from backend.middleware.auth import CurrentUser
from backend.models.appointment import Appointment
from backend.models.availability_slot import AvailabilitySlot
from backend.models.doctor import Doctor
from backend.models.notification import NotificationType
from backend.models.user import User
from backend.schemas.request.internal_request import SendNotificationRequest
from backend.services.notification_service import send_notification
from backend.exceptions.appointment_exception import (
    AppointmentAlreadyBookedException,
    SlotInPastException,
    SlotNotAvailableException,
    SlotNotFoundException,
)
from backend.exceptions.doctor_exception import DoctorNotFoundException
from backend.exceptions.user_exception import UserNotFoundException

logger = logging.getLogger(__name__)


async def book_appointment(
    current_user: CurrentUser,
    slot_id: PydanticObjectId,
) -> Appointment:
    """
    Books an available slot for a patient.
    """

    patient = await User.get(PydanticObjectId(current_user.id))
    if patient is None:
        raise UserNotFoundException(UserMessages.PATIENT_NOT_FOUND)
    
    # basic validation
    slot = await AvailabilitySlot.get(slot_id)
    if slot is None:
        raise SlotNotFoundException()

    if slot.slot_date < date.today():
        raise SlotInPastException()

    doctor = await Doctor.get(slot.doctor_id)
    if doctor is None or not doctor.is_active:
        raise DoctorNotFoundException(DoctorMessages.DOCTOR_INACTIVE_OR_NOT_FOUND)

    """
    Atomically updates the slot from AVAILABLE to BOOKED.
    If the slot is already booked, the update fails and a 409 Conflict is returned,
    preventing double-booking.
    """
    updated_slot = await AvailabilitySlot.get_pymongo_collection().find_one_and_update(
        {"_id": slot_id, "status": SlotStatus.AVAILABLE},
        {"$set": {"status": SlotStatus.BOOKED}},
    )
    if updated_slot is None:
        raise SlotNotAvailableException()

    """
    Creates the appointment after the slot is reserved.
    A unique index on slot_id provides an extra safeguard against duplicate bookings.
    """
    appointment = Appointment(
        patient_id=PydanticObjectId(current_user.id),
        patient_name=patient.full_name,
        patient_email=patient.email,
        patient_phone=patient.phone_number,
        doctor_id=slot.doctor_id,
        doctor_name=doctor.full_name,
        slot_id=slot_id,
        appointment_date=slot.slot_date,
        start_time=slot.start_time,
        end_time=slot.end_time,
        status=AppointmentStatus.BOOKED,
        payment_status=PaymentStatus.MOCK_PAID,
    )

    try:
        await appointment.insert()
    except DuplicateKeyError:
        """
        If appointment creation fails due to a duplicate booking,
        revert the slot back to AVAILABLE to keep the data consistent.
        """
        await AvailabilitySlot.get_motor_collection().update_one(
            {"_id": slot_id},
            {"$set": {"status": SlotStatus.AVAILABLE}},
        )
        raise AppointmentAlreadyBookedException()

    logger.info(
        "Appointment booked: patient=%s doctor=%s slot=%s date=%s",
        current_user.id, slot.doctor_id, slot_id, slot.slot_date,
    )

    # fire-and-forget notification - if it fails the booking is still fine
    await send_notification(SendNotificationRequest(
        recipient_email=patient.email,
        type=NotificationType.APPOINTMENT_CONFIRMATION,
        payload={
            "patient_name": patient.full_name,
            "doctor_name": doctor.full_name,
            "appointment_date": str(slot.slot_date),
            "start_time": slot.start_time,
        },
    ))

    return appointment


async def get_my_appointments(
    patient_id: PydanticObjectId,
    status_filter: AppointmentStatus | None,
) -> list[Appointment]:
    query_filter = {"patient_id": patient_id}
    if status_filter is not None:
        query_filter["status"] = status_filter

    return await Appointment.find(query_filter).sort("-booked_at").to_list()