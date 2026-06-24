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

from backend.constants.roles import Role


def _utc_now() -> datetime:
    # helper so we always store timestamps in UTC, not local time
    return datetime.now(timezone.utc)


class User(Document):
    full_name: str
    email: EmailStr
    password_hash: str
    phone_number: str
    role: Role

    # gender and date of birth only make sense for patients, so they are optional
    gender: Optional[str] = None
    date_of_birth: Optional[date] = None

    is_active: bool = True

    created_at: datetime = Field(default_factory=_utc_now)
    updated_at: datetime = Field(default_factory=_utc_now)

    class Settings:
        name = "users"
        indexes = [
            IndexModel("email", unique=True),
            IndexModel("role"),
        ]