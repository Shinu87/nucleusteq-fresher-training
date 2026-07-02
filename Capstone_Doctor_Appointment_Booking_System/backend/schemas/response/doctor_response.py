"""
Response schemas for the doctor profile / approval workflow.
"""

from datetime import datetime
from typing import Optional

from pydantic import BaseModel

from backend.constants.account_status import AccountStatus
from backend.constants.approval_status import ApprovalStatus


class DoctorProfileResponse(BaseModel):
    """
    Response model containing doctor and user details.
    Used when viewing doctor profile or approval information.
    """
    user_id: str
    full_name: str
    email: str
    phone_number: str
    account_status: AccountStatus

    qualification: str
    specialization: str
    experience_years: int
    license_number: str
    consultation_fee: float
    clinic_address: str

    approval_status: ApprovalStatus
    reviewed_at: Optional[datetime] = None
    created_at: datetime


def to_doctor_profile_response(user, profile) -> "DoctorProfileResponse":
    """
    Converts User and DoctorProfile objects into
    a DoctorProfileResponse object.
    """
    return DoctorProfileResponse(
        user_id=str(user.id),
        full_name=user.full_name,
        email=user.email,
        phone_number=user.phone_number,
        account_status=user.account_status,
        qualification=profile.qualification,
        specialization=profile.specialization,
        experience_years=profile.experience_years,
        license_number=profile.license_number,
        consultation_fee=profile.consultation_fee,
        clinic_address=profile.clinic_address,
        approval_status=profile.approval_status,
        reviewed_at=profile.reviewed_at,
        created_at=user.created_at,
    )
