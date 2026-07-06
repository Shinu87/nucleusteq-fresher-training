"""
Tests for booking_service.py.
"""

from datetime import date, timedelta
from unittest.mock import AsyncMock, patch

import pytest
from beanie import PydanticObjectId
from backend.exceptions.base_exception import AppException

from backend.constants.appointment_status import AppointmentStatus, PaymentStatus
from backend.constants.slot_status import SlotStatus
from backend.middleware.auth import CurrentUser
from backend.models.appointment import Appointment
from backend.models.availability_slot import AvailabilitySlot
from backend.models.doctor import Doctor
from backend.models.user import User
from backend.constants.roles import Role
from backend.services.booking_service import book_appointment, get_my_appointments
from backend.utils.security import hash_password

TOMORROW = date.today() + timedelta(days=1)


async def _make_patient() -> User:
    """Creates a real patient in the test DB."""
    patient = User(
        full_name="Atharv Gokhale",
        email="atharv.gokhale@example.com",
        password_hash=hash_password("Secret@1"),
        phone_number="9876543210",
        role=Role.PATIENT,
    )
    await patient.insert()
    return patient


def _make_current_user(patient: User) -> CurrentUser:
    return CurrentUser(
        id=str(patient.id),
        email=patient.email,
        role=Role.PATIENT,
    )


async def _make_doctor() -> Doctor:
    doctor = Doctor(
        id=PydanticObjectId(),
        full_name="Dr. Aniruddh Kulkarni",
        specialization="Cardiologist",
        qualification="MBBS, MD (Cardiology)",
        experience_years=12,
        consultation_fee=800,
        clinic_address="Baner Road, Pune",
        is_active=True,
    )
    await doctor.insert()
    return doctor


async def _make_slot(
    doctor_id: PydanticObjectId,
    start_time: str = "09:00",
    end_time: str = "09:30",
) -> AvailabilitySlot:
    slot = AvailabilitySlot(
        doctor_id=doctor_id,
        slot_date=TOMORROW,
        start_time=start_time,
        end_time=end_time,
        status=SlotStatus.AVAILABLE,
    )
    await slot.insert()
    return slot


async def test_book_appointment_success(mocker):
    patient = await _make_patient()
    doctor = await _make_doctor()
    slot = await _make_slot(doctor.id)
    current_user = _make_current_user(patient)

    # mock the atomic slot flip to return slot was available
    real_collection = AvailabilitySlot.get_pymongo_collection()
    real_collection.find_one_and_update = AsyncMock(
        return_value={"_id": slot.id, "status": "AVAILABLE"}
    )
    mocker.patch(
        "backend.services.booking_service.send_notification",
        new_callable=AsyncMock,
    )

    appointment = await book_appointment(current_user=current_user, slot_id=slot.id)

    assert appointment.status == AppointmentStatus.BOOKED
    assert appointment.payment_status == PaymentStatus.MOCK_PAID
    assert appointment.consultation_fee == 800
    assert appointment.patient_name == "Atharv Gokhale"
    assert appointment.patient_email == "atharv.gokhale@example.com"
    assert appointment.doctor_name == "Dr. Aniruddh Kulkarni"


async def test_book_appointment_fails_when_slot_already_taken(mocker):
    patient = await _make_patient()
    doctor = await _make_doctor()
    slot = await _make_slot(doctor.id, start_time="10:00", end_time="10:30")
    current_user = _make_current_user(patient)

    # find_one_and_update returning None - slot already BOOKED
    real_collection = AvailabilitySlot.get_pymongo_collection()
    real_collection.find_one_and_update = AsyncMock(return_value=None)

    with pytest.raises(AppException) as exc_info:
        await book_appointment(current_user=current_user, slot_id=slot.id)

    assert exc_info.value.status_code == 409


async def test_book_appointment_fails_for_missing_slot():
    patient = await _make_patient()
    current_user = _make_current_user(patient)

    with pytest.raises(AppException) as exc_info:
        await book_appointment(current_user=current_user, slot_id=PydanticObjectId())

    assert exc_info.value.status_code == 404


async def test_book_appointment_fails_for_past_slot(mocker):
    patient = await _make_patient()
    doctor = await _make_doctor()
    slot = await _make_slot(doctor.id, start_time="11:00", end_time="11:30")
    current_user = _make_current_user(patient)

    future_date = TOMORROW + timedelta(days=1)
    with patch("backend.services.booking_service.date") as mock_date:
        mock_date.today.return_value = future_date

        with pytest.raises(AppException) as exc_info:
            await book_appointment(current_user=current_user, slot_id=slot.id)

    assert exc_info.value.status_code == 400


async def test_get_my_appointments_returns_only_own_appointments():
    patient = await _make_patient()
    doctor = await _make_doctor()
    slot_one = await _make_slot(doctor.id, start_time="09:00", end_time="09:30")
    slot_two = await _make_slot(doctor.id, start_time="10:00", end_time="10:30")

    my_appointment = Appointment(
        patient_id=patient.id,
        patient_name=patient.full_name,
        patient_email=patient.email,
        patient_phone=patient.phone_number,
        doctor_id=doctor.id,
        doctor_name=doctor.full_name,
        slot_id=slot_one.id,
        appointment_date=TOMORROW,
        start_time="09:00",
        end_time="09:30",
        consultation_fee=doctor.consultation_fee,

    )
    await my_appointment.insert()

    # appointment belonging to a different patient - must not appear
    other_appointment = Appointment(
        patient_id=PydanticObjectId(),
        patient_name="Other Patient",
        patient_email="other@example.com",
        patient_phone="1111111111",
        doctor_id=doctor.id,
        doctor_name=doctor.full_name,
        slot_id=slot_two.id,
        appointment_date=TOMORROW,
        start_time="10:00",
        end_time="10:30",
        consultation_fee=doctor.consultation_fee,
    )
    await other_appointment.insert()

    results = await get_my_appointments(patient_id=patient.id, status_filter=None)

    assert len(results) == 1
    assert results[0].patient_id == patient.id
