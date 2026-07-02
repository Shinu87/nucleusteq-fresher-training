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