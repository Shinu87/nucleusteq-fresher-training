"""
All custom exceptions raised across the application, consolidated into
a single file.
"""

from fastapi import status

from backend.constants.appointment_errors import (
    APPOINTMENT_ALREADY_BOOKED,
    APPOINTMENT_NOT_CANCELLABLE,
    APPOINTMENT_NOT_COMPLETABLE,
    APPOINTMENT_NOT_FOUND,
    APPOINTMENT_TIME_NOT_PASSED,
    CANCELLATION_WINDOW_PASSED,
)
from backend.constants.appointment_messages import (
    APPOINTMENT_OWNERSHIP_VIOLATION,
    NOT_CANCELLABLE,
    NOT_COMPLETABLE,
    SLOT_IN_PAST as SLOT_IN_PAST_MESSAGE,
    SLOT_UNAVAILABLE,
    TIME_NOT_PASSED,
)
from backend.constants.appointment_messages import (
    CANCELLATION_WINDOW_PASSED as CANCELLATION_WINDOW_PASSED_MESSAGE,
)

# Appointment exceptions


class SlotInPastException(InvalidOperationException):
    error_code = SLOT_IN_PAST

    def __init__(self, message: str = SLOT_IN_PAST_MESSAGE):
        super().__init__(message)


class AppointmentAlreadyBookedException(AppException):
    status_code = 409
    error_code = APPOINTMENT_ALREADY_BOOKED

    def __init__(self, message: str = SLOT_UNAVAILABLE):
        super().__init__(message)


class AppointmentNotFoundException(ResourceNotFoundException):
    error_code = APPOINTMENT_NOT_FOUND

    def __init__(self, message: str = "Appointment not found"):
        super().__init__(message)


class AppointmentOwnershipException(ForbiddenAccessException):
    error_code = AUTH_FORBIDDEN_ACCESS

    def __init__(self, message: str = APPOINTMENT_OWNERSHIP_VIOLATION):
        super().__init__(message)


class AppointmentNotCancellableException(InvalidOperationException):
    error_code = APPOINTMENT_NOT_CANCELLABLE

    def __init__(self, message: str = NOT_CANCELLABLE):
        super().__init__(message)


class CancellationWindowPassedException(InvalidOperationException):
    error_code = CANCELLATION_WINDOW_PASSED

    def __init__(self, hours: int):
        super().__init__(
            CANCELLATION_WINDOW_PASSED_MESSAGE.format(hours=hours)
        )


class AppointmentNotCompletableException(InvalidOperationException):
    error_code = APPOINTMENT_NOT_COMPLETABLE

    def __init__(self, message: str = NOT_COMPLETABLE):
        super().__init__(message)


class AppointmentTimeNotPassedException(InvalidOperationException):
    error_code = APPOINTMENT_TIME_NOT_PASSED

    def __init__(self, message: str = TIME_NOT_PASSED):
        super().__init__(message)

