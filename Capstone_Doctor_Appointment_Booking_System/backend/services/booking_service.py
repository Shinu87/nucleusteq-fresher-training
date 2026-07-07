"""
Reserve the slot using an atomic MongoDB update.
"""

import logging
from datetime import date, datetime, time, timedelta, timezone

from beanie import PydanticObjectId
from fastapi import Depends, HTTPException, status
from pymongo.errors import DuplicateKeyError

from backend.constants.doctor_messages import DoctorMessages
from backend.constants.user_messages import UserMessages
from backend.constants.appointment_status import AppointmentStatus, PaymentStatus
from backend.constants.validation_constants import ValidationLimits
from backend.middleware.auth import CurrentUser
from backend.models.appointment import Appointment
from backend.models.notification import NotificationType
from backend.repositories.appointment_repository import (
    AppointmentRepository,
    get_appointment_repository,
)
from backend.repositories.availability_repository import (
    AvailabilityRepository,
    get_availability_repository,
)
from backend.repositories.doctor_repository import DoctorRepository, get_doctor_repository
from backend.repositories.user_repository import UserRepository, get_user_repository
from backend.schemas.request.internal_request import SendNotificationRequest
from backend.services.notification_service import NotificationService, get_notification_service
from backend.exceptions.appointment_exception import (
    AppointmentAlreadyBookedException,
    AppointmentNotCancellableException,
    AppointmentNotCompletableException,
    AppointmentNotFoundException,
    AppointmentOwnershipException,
    AppointmentTimeNotPassedException,
    CancellationWindowPassedException,
    SlotInPastException,
)
from backend.exceptions.slot_exception import (
    SlotNotAvailableException,
    SlotNotFoundException
)
from backend.exceptions.doctor_exception import DoctorNotFoundException
from backend.exceptions.user_exception import UserNotFoundException

logger = logging.getLogger(__name__)


def _utc_now() -> datetime:
    return datetime.now(timezone.utc)


class BookingService:

    def __init__(
        self,
        appointment_repository: AppointmentRepository,
        availability_repository: AvailabilityRepository,
        doctor_repository: DoctorRepository,
        user_repository: UserRepository,
        notification_service: NotificationService,
    ):
        self._appointment_repository = appointment_repository
        self._availability_repository = availability_repository
        self._doctor_repository = doctor_repository
        self._user_repository = user_repository
        self._notification_service = notification_service

    async def book_appointment(
        self,
        current_user: CurrentUser,
        slot_id: PydanticObjectId,
    ) -> Appointment:

        patient = await self._user_repository.get_by_id(PydanticObjectId(current_user.id))
        if patient is None:
            raise UserNotFoundException(UserMessages.PATIENT_NOT_FOUND)

        # basic validation
        slot = await self._availability_repository.get_by_id(slot_id)
        if slot is None:
            raise SlotNotFoundException()

        if slot.slot_date < date.today():
            raise SlotInPastException()

        doctor = await self._doctor_repository.get_by_id(slot.doctor_id)
        if doctor is None or not doctor.is_active:
            raise DoctorNotFoundException(DoctorMessages.DOCTOR_INACTIVE_OR_NOT_FOUND)

        updated_slot = await self._availability_repository.atomic_book_if_available(slot_id)
        if updated_slot is None:
            logger.warning(
                "Double booking prevented. patient=%s slot=%s",
                current_user.id,
                slot_id,
            )
            raise SlotNotAvailableException()

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
            await self._appointment_repository.insert(appointment)
        except DuplicateKeyError:
            logger.warning(
                "Duplicate appointment detected. Rolling back slot reservation. slot=%s",
                slot_id,
            )

            await self._availability_repository.revert_to_available_after_duplicate(slot_id)

            logger.info(
                "Slot reverted to AVAILABLE after duplicate booking attempt. slot=%s",
                slot_id,
            )

            raise AppointmentAlreadyBookedException()

        logger.info(
            "Appointment booked: patient=%s doctor=%s slot=%s date=%s",
            current_user.id, slot.doctor_id, slot_id, slot.slot_date,
        )

        await self._notification_service.send_notification(SendNotificationRequest(
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
        self,
        patient_id: PydanticObjectId,
        status_filter: AppointmentStatus | None,
    ) -> list[Appointment]:
        return await self._appointment_repository.find_by_patient(patient_id, status_filter)

    # Appointment Management

    @staticmethod
    def _appointment_datetime(appointment: Appointment) -> datetime:
        hour, minute = (int(part) for part in appointment.start_time.split(":"))
        return datetime.combine(appointment.appointment_date, time(hour=hour, minute=minute))

    async def get_doctor_appointments(
        self,
        doctor_id: PydanticObjectId,
        status_filter: AppointmentStatus | None,
        sort_order: str = "asc",
    ) -> list[Appointment]:
        return await self._appointment_repository.find_by_doctor(doctor_id, status_filter, sort_order)

    async def cancel_appointment(
        self, patient_id: PydanticObjectId, appointment_id: PydanticObjectId
    ) -> Appointment:
        appointment = await self._appointment_repository.get_by_id(appointment_id)
        if appointment is None:
            raise AppointmentNotFoundException()

        if appointment.patient_id != patient_id:
            raise AppointmentOwnershipException()

        if appointment.status != AppointmentStatus.BOOKED:
            raise AppointmentNotCancellableException()

        time_until_appointment = self._appointment_datetime(appointment) - datetime.now()
        if time_until_appointment < timedelta(hours=ValidationLimits.CANCELLATION_WINDOW_HOURS):
            raise CancellationWindowPassedException(ValidationLimits.CANCELLATION_WINDOW_HOURS)

        appointment.status = AppointmentStatus.CANCELLED
        appointment.cancelled_at = _utc_now()
        await self._appointment_repository.save(appointment)

        await self._availability_repository.release_slot(appointment.slot_id)

        logger.info(
            "Appointment cancelled: patient=%s appointment=%s slot=%s",
            patient_id, appointment_id, appointment.slot_id,
        )

        patient = await self._user_repository.get_by_id(patient_id)
        if patient is not None:
            await self._notification_service.send_notification(SendNotificationRequest(
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
        self, doctor_id: PydanticObjectId, appointment_id: PydanticObjectId
    ) -> Appointment:
        appointment = await self._appointment_repository.get_by_id(appointment_id)
        if appointment is None:
            raise AppointmentNotFoundException()

        if appointment.doctor_id != doctor_id:
            raise AppointmentOwnershipException()

        if appointment.status != AppointmentStatus.BOOKED:
            raise AppointmentNotCompletableException()

        if self._appointment_datetime(appointment) > datetime.now():
            raise AppointmentTimeNotPassedException()

        appointment.status = AppointmentStatus.COMPLETED
        appointment.completed_at = _utc_now()
        await self._appointment_repository.save(appointment)

        logger.info(
            "Appointment marked COMPLETED: doctor=%s appointment=%s", doctor_id, appointment_id
        )
        return appointment

    async def mark_appointment_no_show(
        self, doctor_id: PydanticObjectId, appointment_id: PydanticObjectId
    ) -> Appointment:
        appointment = await self._appointment_repository.get_by_id(appointment_id)
        if appointment is None:
            raise AppointmentNotFoundException()

        if appointment.doctor_id != doctor_id:
            raise AppointmentOwnershipException()

        if appointment.status != AppointmentStatus.BOOKED:
            raise AppointmentNotCompletableException()

        if self._appointment_datetime(appointment) > datetime.now():
            raise AppointmentTimeNotPassedException()

        appointment.status = AppointmentStatus.NO_SHOW
        await self._appointment_repository.save(appointment)

        logger.info(
            "Appointment marked NO_SHOW: doctor=%s appointment=%s", doctor_id, appointment_id
        )
        return appointment


def get_booking_service(
    appointment_repository: AppointmentRepository = Depends(get_appointment_repository),
    availability_repository: AvailabilityRepository = Depends(get_availability_repository),
    doctor_repository: DoctorRepository = Depends(get_doctor_repository),
    user_repository: UserRepository = Depends(get_user_repository),
    notification_service: NotificationService = Depends(get_notification_service),
) -> BookingService:
    return BookingService(
        appointment_repository,
        availability_repository,
        doctor_repository,
        user_repository,
        notification_service,
    )