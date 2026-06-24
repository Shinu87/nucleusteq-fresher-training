"""
Request schemas for the registration endpoints. These classes define what
fields we expect from the frontend and run the validation rules
before any of that data touches the database.
"""

import re
from datetime import date
from typing import Literal

from pydantic import BaseModel, EmailStr, field_validator

# name should only be letters and spaces, at least 2 characters long
NAME_REGEX = re.compile(r"^[A-Za-z\s]{2,}$")

# phone number must be exactly 10 digits, nothing else
PHONE_REGEX = re.compile(r"^\d{10}$")

# password must be 8-12 characters, with at least one uppercase letter
# and at least one special character
PASSWORD_REGEX = re.compile(
    r'^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?":{}|<>])[A-Za-z\d!@#$%^&*(),.?":{}|<>]{8,12}$'
)


class _BaseRegisterRequest(BaseModel):
    """
    Both patient and doctor registration share these four fields, so we
    keep the checks here once instead of repeating them in both classes.
    """

    full_name: str
    email: EmailStr
    password: str
    phone_number: str

    @field_validator("full_name")
    @classmethod
    def validate_full_name(cls, value: str) -> str:
        value = value.strip()
        # checking the name only has letters/spaces and is long enough
        if not NAME_REGEX.match(value):
            raise ValueError(
                "Full name must be at least 2 characters long and contain only letters and spaces"
            )        
        return value

    @field_validator("phone_number")
    @classmethod
    def validate_phone_number(cls, value: str) -> str:
        # checking the phone number is exactly 10 digits
        if not PHONE_REGEX.match(value):
            raise ValueError("Phone number must be exactly 10 digits")
        return value

    @field_validator("password")
    @classmethod
    def validate_password(cls, value: str) -> str:
        # checking the password meets the length and complexity rules
        if not PASSWORD_REGEX.match(value):
            raise ValueError(
                "Password must be 8-12 characters long and include at least "
                "one uppercase letter and one special character"
            )
        return value


class PatientRegisterRequest(_BaseRegisterRequest):
    # patients also need to provide these two extra fields
    gender: Literal["MALE", "FEMALE", "OTHER"]
    date_of_birth: date


class DoctorRegisterRequest(_BaseRegisterRequest):
    # doctors only need the shared fields at registration time
    pass