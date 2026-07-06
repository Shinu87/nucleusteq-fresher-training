"""
Reserve the slot using an atomic MongoDB update.

The slot is booked only if it is AVAILABLE, preventing race conditions.
A unique index on slot_id provides an extra safety check to ensure
only one appointment can be created for the slot.
"""

import logging
from datetime import date, datetime, time, timedelta, timezone

from beanie import PydanticObjectId
from fastapi import HTTPException, status
from pymongo.errors import DuplicateKeyError

from backend.constants.message_constants import DoctorMessages, UserMessages
from backend.constants.appointment_status import AppointmentStatus, PaymentStatus
from backend.constants.slot_status import SlotStatus
from backend.constants.validation_constants import ValidationLimits
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
    AppointmentNotCancellableException,
    AppointmentNotCompletableException,
    AppointmentNotFoundException,
    AppointmentOwnershipException,
    AppointmentTimeNotPassedException,
    CancellationWindowPassedException,
    SlotInPastException,
    SlotNotAvailableException,
    SlotNotFoundException,
)
from backend.exceptions.doctor_exception import DoctorNotFoundException
from backend.exceptions.user_exception import UserNotFoundException

logger = logging.getLogger(__name__)


def _utc_now() -> datetime:
    return datetime.now(timezone.utc)


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
        logger.warning(
            "Double booking prevented. patient=%s slot=%s",
            current_user.id,
            slot_id,
        )
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
            consultation_fee=doctor.consultation_fee,
        )

    try:
        await appointment.insert()
    except DuplicateKeyError:
        logger.warning(
            "Duplicate appointment detected. Rolling back slot reservation. slot=%s",
            slot_id,
        )

        await AvailabilitySlot.get_motor_collection().update_one(
            {"_id": slot_id},
            {"$set": {"status": SlotStatus.AVAILABLE}},
        )

        logger.info(
            "Slot reverted to AVAILABLE after duplicate booking attempt. slot=%s",
            slot_id,
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
            "payment_status": PaymentStatus.MOCK_PAID.value,
            "consultation_fee": doctor.consultation_fee,
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

# Appointment Management 

def _appointment_datetime(appointment: Appointment) -> datetime:
    """
    Combines appointment_date + start_time ("HH:MM") into a single naive
    datetime, so it can be compared against datetime.now().
    """
    hour, minute = (int(part) for part in appointment.start_time.split(":"))
    return datetime.combine(appointment.appointment_date, time(hour=hour, minute=minute))


async def get_doctor_appointments(
    doctor_id: PydanticObjectId,
    status_filter: AppointmentStatus | None,
    sort_order: str = "asc",
) -> list[Appointment]:
    """
    Returns a doctor's own appointments, sortable by date.
    """
    query_filter = {"doctor_id": doctor_id}
    if status_filter is not None:
        query_filter["status"] = status_filter

    direction = 1 if sort_order == "asc" else -1
    return (
        await Appointment.find(query_filter)
        .sort([("appointment_date", direction), ("start_time", direction)])
        .to_list()
    )


async def cancel_appointment(
    patient_id: PydanticObjectId, appointment_id: PydanticObjectId
) -> Appointment:
    """
    Cancels a BOOKED appointment, provided the current time is at
    least CANCELLATION_WINDOW_HOURS before the scheduled slot.
    """
    appointment = await Appointment.get(appointment_id)
    if appointment is None:
        raise AppointmentNotFoundException()

    if appointment.patient_id != patient_id:
        raise AppointmentOwnershipException()

    if appointment.status != AppointmentStatus.BOOKED:
        raise AppointmentNotCancellableException()

    time_until_appointment = _appointment_datetime(appointment) - datetime.now()
    if time_until_appointment < timedelta(hours=ValidationLimits.CANCELLATION_WINDOW_HOURS):
        raise CancellationWindowPassedException(ValidationLimits.CANCELLATION_WINDOW_HOURS)

    appointment.status = AppointmentStatus.CANCELLED
    appointment.cancelled_at = _utc_now()
    await appointment.save()

    # slot goes back to AVAILABLE so someone else can book it
    await AvailabilitySlot.get_pymongo_collection().update_one(
        {"_id": appointment.slot_id},
        {"$set": {"status": SlotStatus.AVAILABLE}},
    )

    logger.info(
        "Appointment cancelled: patient=%s appointment=%s slot=%s",
        patient_id, appointment_id, appointment.slot_id,
    )

    patient = await User.get(patient_id)
    if patient is not None:
        await send_notification(SendNotificationRequest(
            recipient_email=patient.email,
            type=NotificationType.APPOINTMENT_CANCELLATION,
            payload={
                "patient_name": appointment.patient_name,
                "doctor_name": appointment.doctor_name,
                "appointment_date": str(appointment.appointment_date),
                "start_time": appointment.start_time,
            },
        ))

    return appointment


async def complete_appointment(
    doctor_id: PydanticObjectId, appointment_id: PydanticObjectId
) -> Appointment:
    """
    Marks a BOOKED appointment as COMPLETED. Blocked until the
    scheduled appointment time has actually passed.
    """
    appointment = await Appointment.get(appointment_id)
    if appointment is None:
        raise AppointmentNotFoundException()

    if appointment.doctor_id != doctor_id:
        raise AppointmentOwnershipException()

    if appointment.status != AppointmentStatus.BOOKED:
        raise AppointmentNotCompletableException()

    if _appointment_datetime(appointment) > datetime.now():
        raise AppointmentTimeNotPassedException()

    appointment.status = AppointmentStatus.COMPLETED
    appointment.completed_at = _utc_now()
    await appointment.save()

    logger.info(
        "Appointment marked COMPLETED: doctor=%s appointment=%s", doctor_id, appointment_id
    )
    return appointment


async def mark_appointment_no_show(
    doctor_id: PydanticObjectId, appointment_id: PydanticObjectId
) -> Appointment:
    """
    Marks a BOOKED appointment as NO_SHOW.
    """
    appointment = await Appointment.get(appointment_id)
    if appointment is None:
        raise AppointmentNotFoundException()

    if appointment.doctor_id != doctor_id:
        raise AppointmentOwnershipException()

    if appointment.status != AppointmentStatus.BOOKED:
        raise AppointmentNotCompletableException()

    if _appointment_datetime(appointment) > datetime.now():
        raise AppointmentTimeNotPassedException()

    appointment.status = AppointmentStatus.NO_SHOW
    await appointment.save()

    logger.info(
        "Appointment marked NO_SHOW: doctor=%s appointment=%s", doctor_id, appointment_id
    )
    return appointment