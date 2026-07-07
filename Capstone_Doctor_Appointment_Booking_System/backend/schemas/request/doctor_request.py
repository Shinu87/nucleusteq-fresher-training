"""
Request schemas specific to the doctor profile / approval workflow.
"""

from typing import Optional

from pydantic import BaseModel, Field, field_validator

from backend.constants.validation_constants import ValidationLimits, ValidationMessages
from backend.schemas.request.auth_request import _BaseRegisterRequest


class DoctorRegisterRequest(_BaseRegisterRequest):

    qualification: str
    specialization: str
    experience_years: int = Field(
        ge=ValidationLimits.MIN_EXPERIENCE_YEARS, le=ValidationLimits.MAX_EXPERIENCE_YEARS
    )
    license_number: str
    consultation_fee: float = Field(gt=ValidationLimits.MIN_CONSULTATION_FEE_EXCLUSIVE)
    clinic_address: str

    @field_validator("qualification", "specialization", "license_number", "clinic_address")
    @classmethod
    def validate_not_blank(cls, value: str) -> str:
        value = value.strip()
        if not value:
            raise ValueError(ValidationMessages.FIELD_CANNOT_BE_BLANK)
        return value


class RejectDoctorRequest(BaseModel):
    reason: Optional[str] = None