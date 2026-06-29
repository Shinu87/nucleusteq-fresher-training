"""
Response schemas for the doctor profile / approval workflow.
"""

from datetime import datetime
from typing import Optional

from pydantic import BaseModel

from user_service.constants.account_status import AccountStatus
from user_service.constants.approval_status import ApprovalStatus


class DoctorProfileResponse(BaseModel):
    """
    Combines fields from
    both the User document and its linked DoctorProfile document, since
    an admin reviewing an application needs to see both at once.
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
    Shared mapper from (User, DoctorProfile) documents to the response
    shape above - used by both auth_router (on registration) and
    admin_router (on list/approve/reject), so the mapping logic lives
    in exactly one place.
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