"""
AvailabilitySlot is one block of time a doctor has marked as open for booking.
"""

from datetime import date, datetime, timezone

from beanie import Document, PydanticObjectId
from pydantic import Field
from pymongo import IndexModel

from backend.constants.slot_status import SlotStatus


def _utc_now() -> datetime:
    return datetime.now(timezone.utc)


class AvailabilitySlot(Document):
    doctor_id: PydanticObjectId

    slot_date: date
    start_time: str
    end_time: str

    status: SlotStatus = Field(default=SlotStatus.AVAILABLE)

    created_at: datetime = Field(default_factory=_utc_now)
    updated_at: datetime = Field(default_factory=_utc_now)

    class Settings:
        name = "availability_slots"
        indexes = [
            IndexModel(
                [("doctor_id", 1), ("slot_date", 1), ("start_time", 1)],
                unique=True,
            ),
            IndexModel([("doctor_id", 1), ("status", 1)]),
        ]