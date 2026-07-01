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

from appointment_service.clients.notification_client import send_booking_confirmation
from appointment_service.constants.appointment_status import AppointmentStatus, PaymentStatus
from appointment_service.constants.slot_status import SlotStatus
from appointment_service.middleware.auth import CurrentUser
from appointment_service.models.appointment import Appointment
from appointment_service.models.availability_slot import AvailabilitySlot
from appointment_service.models.doctor import Doctor

logger = logging.getLogger(__name__)


async def book_appointment(
    current_user: CurrentUser,
    patient_name: str,
    patient_phone: str,
    slot_id: PydanticObjectId,
) -> Appointment:
    """
    Books an available slot for a patient.
    """

    # basic validation
    slot = await AvailabilitySlot.get(slot_id)
    if slot is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Slot not found")

    if slot.slot_date < date.today():
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Cannot book a slot that is already in the past",
        )

    doctor = await Doctor.get(slot.doctor_id)
    if doctor is None or not doctor.is_active:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="The doctor for this slot was not found or is no longer active",
        )

    """
    Atomically updates the slot from AVAILABLE to BOOKED.
    If the slot is already booked, the update fails and a 409 Conflict is returned,
    preventing double-booking.
    """
    updated_slot = await AvailabilitySlot.get_motor_collection().find_one_and_update(
        {"_id": slot_id, "status": SlotStatus.AVAILABLE},
        {"$set": {"status": SlotStatus.BOOKED}},
    )
    if updated_slot is None:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="This slot has already been booked. Please choose another slot.",
        )

    """
    Creates the appointment after the slot is reserved.
    A unique index on slot_id provides an extra safeguard against duplicate bookings.
    """
    appointment = Appointment(
        patient_id=PydanticObjectId(current_user.id),
        patient_name=patient_name,
        patient_email=current_user.email,
        patient_phone=patient_phone,
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
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="This slot has already been booked. Please choose another slot.",
        )

    logger.info(
        "Appointment booked: patient=%s doctor=%s slot=%s date=%s",
        current_user.id, slot.doctor_id, slot_id, slot.slot_date,
    )

    # fire-and-forget notification - if it fails the booking is still fine
    await send_booking_confirmation(
        recipient_email=current_user.email,
        patient_name=patient_name,
        doctor_name=doctor.full_name,
        appointment_date=str(slot.slot_date),
        start_time=slot.start_time,
    )

    return appointment


async def get_my_appointments(
    patient_id: PydanticObjectId,
    status_filter: AppointmentStatus | None,
) -> list[Appointment]:
    query_filter = {"patient_id": patient_id}
    if status_filter is not None:
        query_filter["status"] = status_filter

    return await Appointment.find(query_filter).sort("-booked_at").to_list()