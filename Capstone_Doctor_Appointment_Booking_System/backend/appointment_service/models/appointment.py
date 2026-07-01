"""
Appointment is created when a patient successfully books a doctor's availability slot.
"""

from datetime import date, datetime, timezone
from typing import Optional

from beanie import Document, PydanticObjectId
from pydantic import Field
from pymongo import IndexModel

from appointment_service.constants.appointment_status import AppointmentStatus, PaymentStatus


def _utc_now() -> datetime:
    return datetime.now(timezone.utc)


class Appointment(Document):
    patient_id: PydanticObjectId
    patient_name: str       # denormalized from User Service
    patient_email: str
    patient_phone: str

    doctor_id: PydanticObjectId
    doctor_name: str        # denormalized from Doctor read-model
    slot_id: PydanticObjectId

    appointment_date: date
    start_time: str
    end_time: str

    status: AppointmentStatus = Field(default=AppointmentStatus.BOOKED)
    payment_status: PaymentStatus = Field(default=PaymentStatus.MOCK_PAID)

    booked_at: datetime = Field(default_factory=_utc_now)
    cancelled_at: Optional[datetime] = None
    completed_at: Optional[datetime] = None

    class Settings:
        name = "appointments"
        indexes = [
            # the UNIQUE index on slot_id is our second-layer double-booking
            IndexModel("slot_id", unique=True),
            IndexModel([("patient_id", 1), ("status", 1)]),
            IndexModel([("doctor_id", 1), ("appointment_date", 1)]),
        ]