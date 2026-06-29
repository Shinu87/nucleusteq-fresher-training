"""
AvailabilitySlot is one block of time a doctor has marked as open for
booking. A patient books an appointment by claiming one of these slots.
"""

from datetime import date, datetime, timezone

from beanie import Document, PydanticObjectId
from pydantic import Field
from pymongo import IndexModel

from appointment_service.constants.slot_status import SlotStatus


def _utc_now() -> datetime:
    return datetime.now(timezone.utc)


class AvailabilitySlot(Document):
    doctor_id: PydanticObjectId

    slot_date: date
    start_time: str  # stored as "HH:MM" in 24-hour format, e.g. "09:30"
    end_time: str

    status: SlotStatus = Field(default=SlotStatus.AVAILABLE)

    created_at: datetime = Field(default_factory=_utc_now)
    updated_at: datetime = Field(default_factory=_utc_now)

    class Settings:
        name = "availability_slots"
        indexes = [
            # stops a doctor from accidentally creating the exact same slot twice
            IndexModel(
                [("doctor_id", 1), ("slot_date", 1), ("start_time", 1)],
                unique=True,
            ),
            IndexModel([("doctor_id", 1), ("status", 1)]),
        ]