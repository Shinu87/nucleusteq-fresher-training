"""
Validation-related constants: compiled regex patterns, numeric limits,
and expiry windows.
"""

import re


class ValidationPatterns:
    # name should only be letters and spaces, at least 2 characters long
    NAME_REGEX = re.compile(r"^[A-Za-z\s]{2,}$")

    # phone number must be exactly 10 digits, nothing else
    PHONE_REGEX = re.compile(r"^\d{10}$")

    # password must be 8-12 characters, with at least one uppercase letter
    # and at least one special character
    PASSWORD_REGEX = re.compile(
        r'^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?":{}|<>])[A-Za-z\d!@#$%^&*(),.?":{}|<>]{8,12}$'
    )

    # matches a 24-hour HH:MM time like "09:00" or "17:30"
    TIME_REGEX = re.compile(r"^([01]\d|2[0-3]):([0-5]\d)$")


class ValidationLimits:
    MIN_EXPERIENCE_YEARS = 0
    MAX_EXPERIENCE_YEARS = 70

    # consultation_fee must be strictly greater than this
    MIN_CONSULTATION_FEE_EXCLUSIVE = 0

    # doctor "set password" links expire after this many hours
    SETUP_TOKEN_VALID_HOURS = 24


class ValidationMessages:
    FULL_NAME_INVALID = (
        "Full name must be at least 2 characters long and contain only letters and spaces"
    )
    PHONE_NUMBER_INVALID = "Phone number must be exactly 10 digits"
    PASSWORD_INVALID = (
        "Password must be 8-12 characters long and include at least "
        "one uppercase letter and one special character"
    )
    TIME_FORMAT_INVALID = "Time must be in 24-hour HH:MM format, e.g. '09:30'"
    SLOT_DATE_IN_PAST = "slot_date cannot be in the past"
    END_TIME_BEFORE_START = "end_time must be after start_time"
    FIELD_CANNOT_BE_BLANK = "This field cannot be blank"
