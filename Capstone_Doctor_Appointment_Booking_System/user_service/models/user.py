"""
This is our main User collection in MongoDB. Every person who signs up
(patient or doctor) gets one record here. Doctor-specific stuff like
qualification or experience does not live here, it goes in its own
collection later, so this model only keeps the fields every user shares.
"""

from datetime import date, datetime, timezone
from typing import Optional

from beanie import Document
from pydantic import EmailStr, Field
from pymongo import IndexModel

from user_service.constants.account_status import AccountStatus
from user_service.constants.roles import Role


def _utc_now() -> datetime:
    # helper so we always store timestamps in UTC, not local time
    return datetime.now(timezone.utc)


class User(Document):
    full_name: str
    email: EmailStr
    password_hash: Optional[str] = None
    phone_number: str
    role: Role

    # gender and date of birth only make sense for patients, so they are optional
    gender: Optional[str] = None
    date_of_birth: Optional[date] = None

    # account_status is the LOGIN GATE: patients/admins are ACTIVE right away,
    # but a doctor starts at PENDING_APPROVAL and can't log in until an admin
    # approves them and they set their password
    account_status: AccountStatus = Field(default=AccountStatus.ACTIVE)

    # is_active is a SEPARATE switch admins use later to
    # turn an already-active account on/off, independent of the approval flow
    is_active: bool = True

    created_at: datetime = Field(default_factory=_utc_now)
    updated_at: datetime = Field(default_factory=_utc_now)

    class Settings:
        name = "users"
        indexes = [
            IndexModel("email", unique=True),
            IndexModel("role"),
            IndexModel("account_status"),
        ]