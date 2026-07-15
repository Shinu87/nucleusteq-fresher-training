"""
This is our main User collection in MongoDB.
"""

from datetime import date, datetime, timezone
from typing import Optional

from beanie import Document
from pydantic import EmailStr, Field
from pymongo import IndexModel

from backend.constants.account_status import AccountStatus
from backend.constants.gender import Gender
from backend.constants.roles import Role


def _utc_now() -> datetime:
    return datetime.now(timezone.utc)


class User(Document):
    full_name: str
    email: EmailStr
    password_hash: Optional[str] = None
    phone_number: str
    role: Role

    gender:Optional[Gender] = None
    date_of_birth: Optional[date] = None

    account_status: AccountStatus = Field(default=AccountStatus.ACTIVE)
    created_at: datetime = Field(default_factory=_utc_now)
    updated_at: datetime = Field(default_factory=_utc_now)

    class Settings:
        name = "users"
        indexes = [
            IndexModel("email", unique=True),
            IndexModel("role"),
            IndexModel("account_status"),
        ]