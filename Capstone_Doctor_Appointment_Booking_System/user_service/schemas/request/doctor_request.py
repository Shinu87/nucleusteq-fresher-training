"""
Request schemas specific to the doctor profile / approval workflow.
"""

from typing import Optional

from pydantic import BaseModel, Field, field_validator

from user_service.schemas.request.auth_request import _BaseRegisterRequest


class DoctorRegisterRequest(_BaseRegisterRequest):
    """
    Everything a doctor submits when applying to join the platform.

    Reuses _BaseRegisterRequest's full_name/email/password/phone_number
    fields and validation (shared with patient registration), and adds
    the professional fields requires for doctors specifically.
    """

    qualification: str
    specialization: str
    experience_years: int = Field(ge=0, le=70)
    license_number: str
    consultation_fee: float = Field(gt=0)
    clinic_address: str

    @field_validator("qualification", "specialization", "license_number", "clinic_address")
    @classmethod
    def validate_not_blank(cls, value: str) -> str:
        value = value.strip()
        if not value:
            raise ValueError("This field cannot be blank")
        return value


class RejectDoctorRequest(BaseModel):
    """Optional reason an admin can attach when rejecting an application."""

    reason: Optional[str] = None