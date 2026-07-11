"""
Request schemas specific to the doctor profile / approval workflow.
"""

from typing import Optional

from pydantic import BaseModel, Field, field_validator

from backend.constants.account_status import AccountStatus
from backend.constants.gender import Gender
from backend.constants.specialization import Specialization
from backend.constants.validation_constants import FIELD_CANNOT_BE_BLANK, ValidationLimits
from backend.schemas.request.auth_request import _BaseRegisterRequest
from backend.schemas.request.auth_request import _ContactFields


class DoctorRegisterRequest(_ContactFields):

    qualification: str
    specialization: Specialization
    experience_years: int = Field(
        ge=ValidationLimits.MIN_EXPERIENCE_YEARS, le=ValidationLimits.MAX_EXPERIENCE_YEARS
    )
    license_number: str
    consultation_fee: float = Field(gt=ValidationLimits.MIN_CONSULTATION_FEE_EXCLUSIVE)
    clinic_address: str
    gender: Gender

    @field_validator("qualification", "specialization", "license_number", "clinic_address")
    @classmethod
    def validate_not_blank(cls, value: str) -> str:
        value = value.strip()
        if not value:
            raise ValueError(FIELD_CANNOT_BE_BLANK)
        return value


class RejectDoctorRequest(BaseModel):
    reason: Optional[str] = None

class UpdateAccountStatusRequest(BaseModel):
    account_status: AccountStatus