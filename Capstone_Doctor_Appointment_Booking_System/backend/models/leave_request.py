"""
LeaveRequest is created when a doctor asks to cancel a block of their
own time (an emergency) and is reviewed by an admin before any slots
or appointments are actually touched.
"""

from datetime import date, datetime, timezone
from typing import Optional

from beanie import Document, PydanticObjectId
from pydantic import Field
from pymongo import IndexModel

from backend.constants.leave_request_status import LeaveRequestStatus


def _utc_now() -> datetime:
    return datetime.now(timezone.utc)


class LeaveRequest(Document):
    doctor_id: PydanticObjectId

    date: date
    start_time: str
    end_time: str
    reason: str

    request_status: LeaveRequestStatus = Field(default=LeaveRequestStatus.PENDING)
    rejection_reason: Optional[str] = None

    approved_by: Optional[PydanticObjectId] = None
    approved_at: Optional[datetime] = None

    created_at: datetime = Field(default_factory=_utc_now)
    updated_at: datetime = Field(default_factory=_utc_now)

    class Settings:
        name = "leave_requests"
        indexes = [
            IndexModel("doctor_id"),
            IndexModel("request_status"),
            IndexModel([("doctor_id", 1), ("date", 1)]),
        ]
