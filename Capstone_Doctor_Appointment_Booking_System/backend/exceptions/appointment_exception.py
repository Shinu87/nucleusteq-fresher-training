"""
Exceptions raised for appointment management.
"""

from backend.constants.appointment_errors import AppointmentErrorCode
from backend.constants.slot_errors import SlotErrorCode
from backend.constants.auth_errors import AuthErrorCode
from backend.constants.appointment_messages import AppointmentMessages
from backend.exceptions.base_exception import (
    AppException,
    ForbiddenAccessException,
    InvalidOperationException,
    ResourceNotFoundException,
)


class SlotInPastException(InvalidOperationException):
    error_code = SlotErrorCode.SLOT_IN_PAST

    def __init__(self, message: str = AppointmentMessages.SLOT_IN_PAST):
        super().__init__(message)


class AppointmentAlreadyBookedException(AppException):
    status_code = 409
    error_code = AppointmentErrorCode.APPOINTMENT_ALREADY_BOOKED

    def __init__(self, message: str = AppointmentMessages.SLOT_UNAVAILABLE):
        super().__init__(message)


class AppointmentNotFoundException(ResourceNotFoundException):
    error_code = AppointmentErrorCode.APPOINTMENT_NOT_FOUND

    def __init__(self, message: str = "Appointment not found"):
        super().__init__(message)


class AppointmentOwnershipException(ForbiddenAccessException):
    error_code = AuthErrorCode.FORBIDDEN_ACCESS

    def __init__(self, message: str = AppointmentMessages.APPOINTMENT_OWNERSHIP_VIOLATION):
        super().__init__(message)


class AppointmentNotCancellableException(InvalidOperationException):
    error_code = AppointmentErrorCode.APPOINTMENT_NOT_CANCELLABLE

    def __init__(self, message: str = AppointmentMessages.NOT_CANCELLABLE):
        super().__init__(message)


class CancellationWindowPassedException(InvalidOperationException):
    error_code = AppointmentErrorCode.CANCELLATION_WINDOW_PASSED

    def __init__(self, hours: int):
        super().__init__(
            AppointmentMessages.CANCELLATION_WINDOW_PASSED.format(hours=hours)
        )


class AppointmentNotCompletableException(InvalidOperationException):
    error_code = AppointmentErrorCode.APPOINTMENT_NOT_COMPLETABLE

    def __init__(self, message: str = AppointmentMessages.NOT_COMPLETABLE):
        super().__init__(message)


class AppointmentTimeNotPassedException(InvalidOperationException):
    error_code = AppointmentErrorCode.APPOINTMENT_TIME_NOT_PASSED

    def __init__(self, message: str = AppointmentMessages.TIME_NOT_PASSED):
        super().__init__(message)