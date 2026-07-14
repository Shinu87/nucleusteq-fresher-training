from datetime import date, datetime, timedelta
from types import SimpleNamespace
from unittest.mock import AsyncMock

import pytest
from beanie import PydanticObjectId
from pymongo.errors import DuplicateKeyError

from backend.constants.appointment_status import AppointmentStatus, PaymentStatus
from backend.constants.slot_status import SlotStatus
from backend.exceptions.custom_exceptions import (
    AppointmentAlreadyBookedException,
    DoctorNotFoundException,
    SlotInPastException,
    SlotNotAvailableException,
    SlotNotFoundException,
    UserNotFoundException,
)
from backend.models.appointment import Appointment
from backend.models.availability_slot import AvailabilitySlot
from backend.services.booking_service import BookingService
from datetime import datetime, timedelta

from backend.constants.validation_constants import ValidationLimits
from backend.exceptions.custom_exceptions import (
    AppointmentNotCancellableException,
    AppointmentNotFoundException,
    AppointmentOwnershipException,
    CancellationWindowPassedException,
)



# Fixtures

@pytest.fixture
def appointment_repository():
    return AsyncMock()


@pytest.fixture
def availability_repository():
    return AsyncMock()


@pytest.fixture
def doctor_repository():
    return AsyncMock()


@pytest.fixture
def user_repository():
    return AsyncMock()


@pytest.fixture
def notification_service():
    return AsyncMock()


@pytest.fixture
def booking_service(
    appointment_repository,
    availability_repository,
    doctor_repository,
    user_repository,
    notification_service,
):
    return BookingService(
        appointment_repository,
        availability_repository,
        doctor_repository,
        user_repository,
        notification_service,
    )


@pytest.fixture
def patient():
    patient = AsyncMock()
    patient.id = PydanticObjectId()
    patient.full_name = "John Doe"
    patient.email = "john@example.com"
    patient.phone_number = "9876543210"
    return patient


@pytest.fixture
def doctor():
    doctor = AsyncMock()
    doctor.id = PydanticObjectId()
    doctor.full_name = "Dr. Sharma"
    doctor.consultation_fee = 500
    doctor.is_active = True
    return doctor


@pytest.fixture
def slot(doctor):
    return AvailabilitySlot(
        id=PydanticObjectId(),
        doctor_id=doctor.id,
        slot_date=date.today() + timedelta(days=1),
        start_time="10:00",
        end_time="10:30",
        status=SlotStatus.AVAILABLE,
    )


@pytest.fixture
def current_user(patient):
    return SimpleNamespace(id=str(patient.id))


# book_appointment()

async def test_book_appointment_success(
    booking_service,
    appointment_repository,
    availability_repository,
    doctor_repository,
    user_repository,
    notification_service,
    patient,
    doctor,
    slot,
    current_user,
):
    user_repository.get_by_id.return_value = patient

    availability_repository.get_by_id.return_value = slot
    availability_repository.atomic_book_if_available.return_value = slot

    doctor_repository.get_by_id.return_value = doctor

    appointment = await booking_service.book_appointment(
        current_user,
        slot.id,
    )

    assert isinstance(appointment, Appointment)

    assert appointment.patient_id == patient.id
    assert appointment.doctor_id == doctor.id

    assert appointment.status == AppointmentStatus.BOOKED
    assert appointment.payment_status == PaymentStatus.MOCK_PAID

    appointment_repository.insert.assert_awaited_once()
    notification_service.send_notification.assert_awaited_once()


async def test_book_appointment_patient_not_found(
    booking_service,
    user_repository,
    current_user,
):
    user_repository.get_by_id.return_value = None

    with pytest.raises(UserNotFoundException):
        await booking_service.book_appointment(
            current_user,
            PydanticObjectId(),
        )


async def test_book_appointment_slot_not_found(
    booking_service,
    user_repository,
    availability_repository,
    patient,
    current_user,
):
    user_repository.get_by_id.return_value = patient
    availability_repository.get_by_id.return_value = None

    with pytest.raises(SlotNotFoundException):
        await booking_service.book_appointment(
            current_user,
            PydanticObjectId(),
        )


async def test_book_appointment_slot_in_past(
    booking_service,
    user_repository,
    availability_repository,
    patient,
    doctor,
    current_user,
):
    user_repository.get_by_id.return_value = patient

    expired_slot = AvailabilitySlot(
        doctor_id=doctor.id,
        slot_date=date.today() - timedelta(days=1),
        start_time="09:00",
        end_time="09:30",
        status=SlotStatus.AVAILABLE,
    )

    availability_repository.get_by_id.return_value = expired_slot

    with pytest.raises(SlotInPastException):
        await booking_service.book_appointment(
            current_user,
            expired_slot.id,
        )


async def test_book_appointment_doctor_not_found(
    booking_service,
    user_repository,
    availability_repository,
    doctor_repository,
    patient,
    slot,
    current_user,
):
    user_repository.get_by_id.return_value = patient

    availability_repository.get_by_id.return_value = slot

    doctor_repository.get_by_id.return_value = None

    with pytest.raises(DoctorNotFoundException):
        await booking_service.book_appointment(
            current_user,
            slot.id,
        )


async def test_book_appointment_doctor_inactive(
    booking_service,
    user_repository,
    availability_repository,
    doctor_repository,
    patient,
    doctor,
    slot,
    current_user,
):
    user_repository.get_by_id.return_value = patient

    availability_repository.get_by_id.return_value = slot

    doctor.is_active = False
    doctor_repository.get_by_id.return_value = doctor

    with pytest.raises(DoctorNotFoundException):
        await booking_service.book_appointment(
            current_user,
            slot.id,
        )


async def test_book_appointment_slot_already_booked(
    booking_service,
    user_repository,
    availability_repository,
    doctor_repository,
    patient,
    doctor,
    slot,
    current_user,
):
    user_repository.get_by_id.return_value = patient

    availability_repository.get_by_id.return_value = slot

    doctor_repository.get_by_id.return_value = doctor

    availability_repository.atomic_book_if_available.return_value = None

    with pytest.raises(SlotNotAvailableException):
        await booking_service.book_appointment(
            current_user,
            slot.id,
        )


async def test_book_appointment_duplicate_insert_rolls_back_slot(
    booking_service,
    appointment_repository,
    availability_repository,
    doctor_repository,
    user_repository,
    patient,
    doctor,
    slot,
    current_user,
):
    user_repository.get_by_id.return_value = patient

    availability_repository.get_by_id.return_value = slot
    availability_repository.atomic_book_if_available.return_value = slot

    doctor_repository.get_by_id.return_value = doctor

    appointment_repository.insert.side_effect = DuplicateKeyError(
        "duplicate"
    )

    with pytest.raises(AppointmentAlreadyBookedException):
        await booking_service.book_appointment(
            current_user,
            slot.id,
        )

    availability_repository.revert_to_available_after_duplicate.assert_awaited_once_with(
        slot.id
    )


# get_my_appointments()


async def test_get_my_appointments(
    booking_service,
    appointment_repository,
):
    patient_id = PydanticObjectId()

    appointments = [
        AsyncMock(),
        AsyncMock(),
    ]

    appointment_repository.find_by_patient.return_value = appointments

    result = await booking_service.get_my_appointments(
        patient_id,
        None,
    )

    assert result == appointments

    appointment_repository.find_by_patient.assert_awaited_once_with(
        patient_id,
        None,
    )


# _appointment_datetime()

def test_appointment_datetime():
    appointment = AsyncMock()

    appointment.appointment_date = date(2026, 8, 1)
    appointment.start_time = "14:30"

    dt = BookingService._appointment_datetime(
        appointment,
    )

    assert dt == datetime(
        2026,
        8,
        1,
        14,
        30,
    )


# get_doctor_appointments()

async def test_get_doctor_appointments(
    booking_service,
    appointment_repository,
):
    doctor_id = PydanticObjectId()

    appointments = [
        AsyncMock(),
    ]

    appointment_repository.find_by_doctor.return_value = appointments

    result = await booking_service.get_doctor_appointments(
        doctor_id,
        AppointmentStatus.BOOKED,
    )

    assert result == appointments

    appointment_repository.find_by_doctor.assert_awaited_once_with(
        doctor_id,
        AppointmentStatus.BOOKED,
        "asc",
    )


# cancel_appointment()

async def test_cancel_appointment_success(
    booking_service,
    appointment_repository,
    availability_repository,
    user_repository,
    notification_service,
):
    patient_id = PydanticObjectId()

    appointment = AsyncMock()

    appointment.id = PydanticObjectId()
    appointment.patient_id = patient_id
    appointment.slot_id = PydanticObjectId()

    appointment.patient_name = "John"

    appointment.doctor_name = "Dr. Sharma"

    appointment.appointment_date = date.today() + timedelta(days=2)

    appointment.start_time = "10:00"

    appointment.status = AppointmentStatus.BOOKED

    appointment_repository.get_by_id.return_value = appointment

    patient = AsyncMock()

    patient.email = "john@example.com"

    user_repository.get_by_id.return_value = patient

    result = await booking_service.cancel_appointment(
        patient_id,
        appointment.id,
    )

    assert result.status == AppointmentStatus.CANCELLED

    appointment_repository.save.assert_awaited_once_with(
        appointment,
    )

    availability_repository.release_slot.assert_awaited_once_with(
        appointment.slot_id,
    )

    notification_service.send_notification.assert_awaited_once()


async def test_cancel_appointment_not_found(
    booking_service,
    appointment_repository,
):
    appointment_repository.get_by_id.return_value = None

    with pytest.raises(
        AppointmentNotFoundException,
    ):
        await booking_service.cancel_appointment(
            PydanticObjectId(),
            PydanticObjectId(),
        )


async def test_cancel_appointment_wrong_patient(
    booking_service,
    appointment_repository,
):
    appointment = AsyncMock()

    appointment.patient_id = PydanticObjectId()

    appointment_repository.get_by_id.return_value = appointment

    with pytest.raises(
        AppointmentOwnershipException,
    ):
        await booking_service.cancel_appointment(
            PydanticObjectId(),
            PydanticObjectId(),
        )


async def test_cancel_appointment_not_booked(
    booking_service,
    appointment_repository,
):
    appointment = AsyncMock()

    appointment.patient_id = PydanticObjectId()

    appointment.status = AppointmentStatus.CANCELLED

    appointment_repository.get_by_id.return_value = appointment

    with pytest.raises(
        AppointmentNotCancellableException,
    ):
        await booking_service.cancel_appointment(
            appointment.patient_id,
            PydanticObjectId(),
        )


async def test_cancel_appointment_after_window(
    booking_service,
    appointment_repository,
):
    patient_id = PydanticObjectId()

    appointment = AsyncMock()

    appointment.patient_id = patient_id

    appointment.status = AppointmentStatus.BOOKED

    appointment.appointment_date = date.today()

    appointment.start_time = (
        datetime.now() +
        timedelta(
            hours=ValidationLimits.CANCELLATION_WINDOW_HOURS - 1
        )
    ).strftime("%H:%M")

    appointment_repository.get_by_id.return_value = appointment

    with pytest.raises(
        CancellationWindowPassedException,
    ):
        await booking_service.cancel_appointment(
            patient_id,
            PydanticObjectId(),
        )


async def test_cancel_appointment_patient_deleted(
    booking_service,
    appointment_repository,
    availability_repository,
    user_repository,
    notification_service,
):
    patient_id = PydanticObjectId()

    appointment = AsyncMock()

    appointment.patient_id = patient_id

    appointment.slot_id = PydanticObjectId()

    appointment.patient_name = "John"

    appointment.doctor_name = "Dr. Sharma"

    appointment.appointment_date = date.today() + timedelta(days=2)

    appointment.start_time = "09:00"

    appointment.status = AppointmentStatus.BOOKED

    appointment_repository.get_by_id.return_value = appointment

    user_repository.get_by_id.return_value = None

    await booking_service.cancel_appointment(
        patient_id,
        PydanticObjectId(),
    )

    availability_repository.release_slot.assert_awaited_once()

    notification_service.send_notification.assert_not_awaited()

from backend.exceptions.custom_exceptions import (
    AppointmentNotCompletableException,
    AppointmentTimeNotPassedException,
)


# complete_appointment()

async def test_complete_appointment_success(
    booking_service,
    appointment_repository,
):
    doctor_id = PydanticObjectId()

    appointment = AsyncMock()
    appointment.id = PydanticObjectId()
    appointment.doctor_id = doctor_id
    appointment.status = AppointmentStatus.BOOKED
    appointment.appointment_date = date.today() - timedelta(days=1)
    appointment.start_time = "09:00"

    appointment_repository.get_by_id.return_value = appointment

    result = await booking_service.complete_appointment(
        doctor_id,
        appointment.id,
    )

    assert result.status == AppointmentStatus.COMPLETED
    assert result.completed_at is not None

    appointment_repository.save.assert_awaited_once_with(
        appointment,
    )


async def test_complete_appointment_not_found(
    booking_service,
    appointment_repository,
):
    appointment_repository.get_by_id.return_value = None

    with pytest.raises(AppointmentNotFoundException):
        await booking_service.complete_appointment(
            PydanticObjectId(),
            PydanticObjectId(),
        )


async def test_complete_appointment_wrong_doctor(
    booking_service,
    appointment_repository,
):
    appointment = AsyncMock()
    appointment.doctor_id = PydanticObjectId()

    appointment_repository.get_by_id.return_value = appointment

    with pytest.raises(AppointmentOwnershipException):
        await booking_service.complete_appointment(
            PydanticObjectId(),
            PydanticObjectId(),
        )


async def test_complete_appointment_not_booked(
    booking_service,
    appointment_repository,
):
    doctor_id = PydanticObjectId()

    appointment = AsyncMock()
    appointment.doctor_id = doctor_id
    appointment.status = AppointmentStatus.CANCELLED

    appointment_repository.get_by_id.return_value = appointment

    with pytest.raises(AppointmentNotCompletableException):
        await booking_service.complete_appointment(
            doctor_id,
            PydanticObjectId(),
        )


async def test_complete_appointment_before_time(
    booking_service,
    appointment_repository,
):
    doctor_id = PydanticObjectId()

    appointment = AsyncMock()
    appointment.doctor_id = doctor_id
    appointment.status = AppointmentStatus.BOOKED
    appointment.appointment_date = date.today() + timedelta(days=1)
    appointment.start_time = "10:00"

    appointment_repository.get_by_id.return_value = appointment

    with pytest.raises(AppointmentTimeNotPassedException):
        await booking_service.complete_appointment(
            doctor_id,
            PydanticObjectId(),
        )


# mark_appointment_no_show()

async def test_mark_no_show_success(
    booking_service,
    appointment_repository,
):
    doctor_id = PydanticObjectId()

    appointment = AsyncMock()
    appointment.id = PydanticObjectId()
    appointment.doctor_id = doctor_id
    appointment.status = AppointmentStatus.BOOKED
    appointment.appointment_date = date.today() - timedelta(days=1)
    appointment.start_time = "09:00"

    appointment_repository.get_by_id.return_value = appointment

    result = await booking_service.mark_appointment_no_show(
        doctor_id,
        appointment.id,
    )

    assert result.status == AppointmentStatus.NO_SHOW

    appointment_repository.save.assert_awaited_once_with(
        appointment,
    )


async def test_mark_no_show_not_found(
    booking_service,
    appointment_repository,
):
    appointment_repository.get_by_id.return_value = None

    with pytest.raises(AppointmentNotFoundException):
        await booking_service.mark_appointment_no_show(
            PydanticObjectId(),
            PydanticObjectId(),
        )


async def test_mark_no_show_wrong_doctor(
    booking_service,
    appointment_repository,
):
    appointment = AsyncMock()
    appointment.doctor_id = PydanticObjectId()

    appointment_repository.get_by_id.return_value = appointment

    with pytest.raises(AppointmentOwnershipException):
        await booking_service.mark_appointment_no_show(
            PydanticObjectId(),
            PydanticObjectId(),
        )


async def test_mark_no_show_not_booked(
    booking_service,
    appointment_repository,
):
    doctor_id = PydanticObjectId()

    appointment = AsyncMock()
    appointment.doctor_id = doctor_id
    appointment.status = AppointmentStatus.COMPLETED

    appointment_repository.get_by_id.return_value = appointment

    with pytest.raises(AppointmentNotCompletableException):
        await booking_service.mark_appointment_no_show(
            doctor_id,
            PydanticObjectId(),
        )


async def test_mark_no_show_before_time(
    booking_service,
    appointment_repository,
):
    doctor_id = PydanticObjectId()

    appointment = AsyncMock()
    appointment.doctor_id = doctor_id
    appointment.status = AppointmentStatus.BOOKED
    appointment.appointment_date = date.today() + timedelta(days=1)
    appointment.start_time = "09:00"

    appointment_repository.get_by_id.return_value = appointment

    with pytest.raises(AppointmentTimeNotPassedException):
        await booking_service.mark_appointment_no_show(
            doctor_id,
            PydanticObjectId(),
        )