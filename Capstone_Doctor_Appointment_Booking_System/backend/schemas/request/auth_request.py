"""
Request schemas for the registration endpoints.
"""

import re
from datetime import date
from typing import Literal

from pydantic import BaseModel, EmailStr, field_validator

from backend.constants.validation_constants import ValidationMessages, ValidationPatterns


class _BaseRegisterRequest(BaseModel):

    full_name: str
    email: EmailStr
    password: str
    phone_number: str

    @field_validator("full_name")
    @classmethod
    def validate_full_name(cls, value: str) -> str:
        value = value.strip()
        if not ValidationPatterns.NAME_REGEX.match(value):
            raise ValueError(ValidationMessages.FULL_NAME_INVALID)      
        return value

    @field_validator("phone_number")
    @classmethod
    def validate_phone_number(cls, value: str) -> str:
        if not ValidationPatterns.PHONE_REGEX.match(value):
            raise ValueError(ValidationMessages.PHONE_NUMBER_INVALID)
        return value

    @field_validator("password")
    @classmethod
    def validate_password(cls, value: str) -> str:
        if not ValidationPatterns.PASSWORD_REGEX.match(value):
            raise ValueError(ValidationMessages.PASSWORD_INVALID)
        return value


class PatientRegisterRequest(_BaseRegisterRequest):
    gender: Literal["MALE", "FEMALE", "OTHER"]
    date_of_birth: date


class DoctorRegisterRequest(_BaseRegisterRequest):
    pass

class LoginRequest(BaseModel):
    email: EmailStr
    password: str

class SetPasswordRequest(BaseModel):
    token: str
    new_password: str

    @field_validator("new_password")
    @classmethod
    def validate_new_password(cls, value: str) -> str:
        if not ValidationPatterns.PASSWORD_REGEX.match(value):
            raise ValueError(ValidationMessages.PASSWORD_INVALID)
        return value


