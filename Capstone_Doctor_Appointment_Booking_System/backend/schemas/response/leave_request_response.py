"""
Response schema for the doctor leave-request workflow.
"""

from datetime import date, datetime
from typing import Optional

from pydantic import BaseModel

from backend.constants.leave_request_status import LeaveRequestStatus


class LeaveRequestResponse(BaseModel):

    id: str
    doctor_id: str
    date: date
    start_time: str
    end_time: str
    reason: str
    request_status: LeaveRequestStatus
    rejection_reason: Optional[str] = None
    approved_by: Optional[str] = None
    approved_at: Optional[datetime] = None
    created_at: datetime
    updated_at: datetime


def to_leave_request_response(leave_request) -> "LeaveRequestResponse":
    return LeaveRequestResponse(
        id=str(leave_request.id),
        doctor_id=str(leave_request.doctor_id),
        date=leave_request.date,
        start_time=leave_request.start_time,
        end_time=leave_request.end_time,
        reason=leave_request.reason,
        request_status=leave_request.request_status,
        rejection_reason=leave_request.rejection_reason,
        approved_by=str(leave_request.approved_by) if leave_request.approved_by else None,
        approved_at=leave_request.approved_at,
        created_at=leave_request.created_at,
        updated_at=leave_request.updated_at,
    )
