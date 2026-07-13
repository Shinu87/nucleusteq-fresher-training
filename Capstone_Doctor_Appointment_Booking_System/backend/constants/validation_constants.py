from types import SimpleNamespace
import re

ValidationPatterns = SimpleNamespace(
    NAME_REGEX=re.compile(r"^[A-Za-z\s]{2,}$"),
    PHONE_REGEX=re.compile(r"^\d{10}$"),
    PASSWORD_REGEX=re.compile(
        r'^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?":{}|<>])[A-Za-z\d!@#$%^&*(),.?":{}|<>]{8,12}$'
    ),
    TIME_REGEX=re.compile(r"^([01]\d|2[0-3]):([0-5]\d)$"),
    EMAIL_REGEX=re.compile(
        r"^[A-Za-z0-9](?:[A-Za-z0-9._%+-]*[A-Za-z0-9])?@"
        r"(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\.)+"
        r"[A-Za-z]{2,}$"
    ),
)

ValidationLimits = SimpleNamespace(
    MIN_EXPERIENCE_YEARS=0,
    MAX_EXPERIENCE_YEARS=70,
    MIN_CONSULTATION_FEE_EXCLUSIVE=0,
    SETUP_TOKEN_VALID_HOURS=24,
    MIN_SLOT_DURATION_MINUTES=5,
    MAX_SLOT_DURATION_MINUTES=120,
    CANCELLATION_WINDOW_HOURS=2,
)

ValidationMessages = SimpleNamespace(
    FULL_NAME_INVALID="Full name must be at least 2 characters long and contain only letters and spaces",
    PHONE_NUMBER_INVALID="Phone number must be exactly 10 digits",
    PASSWORD_INVALID=(
        "Password must be 8-12 characters long and include at least "
        "one uppercase letter and one special character"
    ),
    TIME_FORMAT_INVALID="Time must be in 24-hour HH:MM format, e.g. '09:30'",
    SLOT_DATE_IN_PAST="slot_date cannot be in the past",
    END_TIME_BEFORE_START="end_time must be after start_time",
    FIELD_CANNOT_BE_BLANK="This field cannot be blank",
    DURATION_OUT_OF_RANGE="duration_minutes must be between {min} and {max} minutes",
)