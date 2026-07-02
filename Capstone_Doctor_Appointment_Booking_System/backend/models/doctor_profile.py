"""
DoctorProfile holds everything specific to being a doctor: qualification,
specialization, license number, consultation fee, clinic address, and
where their application currently stands in the admin approval process.
"""

from datetime import datetime, timezone
from typing import Optional

from beanie import Document, PydanticObjectId
from pydantic import Field
from pymongo import IndexModel

from backend.constants.approval_status import ApprovalStatus


def _utc_now() -> datetime:
    return datetime.now(timezone.utc)


class DoctorProfile(Document):
    # links back to the matching User document (role=DOCTOR)
    user_id: PydanticObjectId

    qualification: str
    specialization: str
    experience_years: int
    license_number: str
    consultation_fee: float
    clinic_address: str

    approval_status: ApprovalStatus = Field(default=ApprovalStatus.PENDING_APPROVAL)
    reviewed_by: Optional[PydanticObjectId] = None  # which admin reviewed it
    reviewed_at: Optional[datetime] = None

    # set when an admin approves - used once by /auth/set-password, then cleared
    setup_token_hash: Optional[str] = None
    setup_token_expiry: Optional[datetime] = None

    created_at: datetime = Field(default_factory=_utc_now)
    updated_at: datetime = Field(default_factory=_utc_now)

    class Settings:
        name = "doctor_profiles"
        indexes = [
            IndexModel("user_id", unique=True),
            IndexModel("license_number", unique=True),
            IndexModel("approval_status"),
        ]