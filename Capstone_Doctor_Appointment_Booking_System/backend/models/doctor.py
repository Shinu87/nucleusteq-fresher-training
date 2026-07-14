"""
Doctor stores only the information needed for doctor search and
appointment booking. Keeping it separate makes booking-related
operations simpler and avoids using admin-specific data.
"""

from datetime import datetime, timezone

from beanie import Document, PydanticObjectId
from pydantic import Field
from pymongo import IndexModel

from backend.constants.specialization import Specialization

def _utc_now() -> datetime:
    return datetime.now(timezone.utc)


class Doctor(Document):
    id: PydanticObjectId

    full_name: str
    specialization: Specialization
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
            IndexModel([("full_name", "text"), ("specialization", "text")]),
]