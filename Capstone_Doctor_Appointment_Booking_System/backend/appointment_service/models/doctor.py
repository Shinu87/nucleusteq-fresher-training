"""
Doctor is NOT the main source of truth here.

This is just a simple local copy inside Appointment Service.

We only keep the fields needed so patients can search and book doctors easily.
This data is kept updated using the /internal/doctors sync endpoint.

The _id is kept the same as the doctor _id in User Service (users collection),
so both services can be linked easily without any extra mapping table.
"""

from datetime import datetime, timezone

from beanie import Document, PydanticObjectId
from pydantic import Field
from pymongo import IndexModel


def _utc_now() -> datetime:
    return datetime.now(timezone.utc)


class Doctor(Document):
    id: PydanticObjectId  # same value as the doctor's id in User Service

    full_name: str
    specialization: str
    qualification: str
    experience_years: int
    consultation_fee: float
    clinic_address: str

    is_active: bool = True
    synced_at: datetime = Field(default_factory=_utc_now)

    class Settings:
        name = "doctors"
        indexes = [
            IndexModel("specialization"),
            IndexModel("is_active"),
            # text index so GET /doctors can search by name or specialization
            # in one query
            IndexModel([("full_name", "text"), ("specialization", "text")]),
]