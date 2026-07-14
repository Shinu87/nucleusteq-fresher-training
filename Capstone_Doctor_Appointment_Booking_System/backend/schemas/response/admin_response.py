"""
Response schemas for the admin module
"""

from datetime import date, datetime
from typing import Optional

from pydantic import BaseModel

from backend.constants.account_status import AccountStatus


class PatientResponse(BaseModel):
    id: str
    full_name: str
    email: str
    phone_number: str
    gender: Optional[str] = None
    date_of_birth: Optional[date] = None
    account_status: AccountStatus
    created_at: datetime


def to_patient_response(user) -> "PatientResponse":
    return PatientResponse(
        id=str(user.id),
        full_name=user.full_name,
        email=user.email,
        phone_number=user.phone_number,
        gender=user.gender,
        date_of_birth=user.date_of_birth,
        account_status=user.account_status,
        created_at=user.created_at,
    )


class AdminStatsResponse(BaseModel):
    total_doctors: int
    active_doctors: int
    total_patients: int
    total_appointments: int
    completed_appointments: int
    cancelled_appointments: int