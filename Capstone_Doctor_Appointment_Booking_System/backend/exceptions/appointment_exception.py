"""
Exceptions raised for availability slots and appointment booking.
"""

from fastapi import status

from backend.constants.error_constants import ErrorCode
from backend.constants.message_constants import AppointmentMessages, SlotMessages
from backend.exceptions.base_exception import (
    AppException,
    DuplicateResourceException,
    ForbiddenAccessException,
    InvalidOperationException,
    ResourceNotFoundException,
)


class SlotNotFoundException(ResourceNotFoundException):

    error_code = ErrorCode.SLOT_NOT_FOUND

    def __init__(self, message: str = SlotMessages.SLOT_NOT_FOUND):
        super().__init__(message)


class SlotOwnershipException(ForbiddenAccessException):

    error_code = ErrorCode.FORBIDDEN_ACCESS

    def __init__(self, message: str = SlotMessages.SLOT_OWNERSHIP_VIOLATION):
        super().__init__(message)


class SlotNotEditableException(InvalidOperationException):

    error_code = ErrorCode.SLOT_NOT_EDITABLE

    def __init__(self, message: str = SlotMessages.SLOT_NOT_EDITABLE):
        super().__init__(message)


class SlotNotDeletableException(InvalidOperationException):

    error_code = ErrorCode.SLOT_NOT_DELETABLE

    def __init__(self, message: str = SlotMessages.SLOT_NOT_DELETABLE):
        super().__init__(message)


class InvalidSlotTimeRangeException(InvalidOperationException):

    error_code = ErrorCode.INVALID_OPERATION

    def __init__(self, message: str = SlotMessages.INVALID_TIME_RANGE):
        super().__init__(message)


class DuplicateSlotException(DuplicateResourceException):

    error_code = ErrorCode.DUPLICATE_SLOT

    def __init__(self, message: str = SlotMessages.DUPLICATE_SLOT):
        super().__init__(message)

class NoSlotsGeneratedException(InvalidOperationException):

    error_code = ErrorCode.NO_SLOTS_GENERATED

    def __init__(self, message: str = SlotMessages.NO_SLOTS_GENERATED):
        super().__init__(message)

class SlotInPastException(InvalidOperationException):

    error_code = ErrorCode.SLOT_IN_PAST

    def __init__(self, message: str = AppointmentMessages.SLOT_IN_PAST):
        super().__init__(message)


class SlotNotAvailableException(AppException):

    status_code = status.HTTP_409_CONFLICT
    error_code = ErrorCode.SLOT_NOT_AVAILABLE

    def __init__(self, message: str = AppointmentMessages.SLOT_UNAVAILABLE):
        super().__init__(message)


class AppointmentAlreadyBookedException(AppException):

    status_code = status.HTTP_409_CONFLICT
    error_code = ErrorCode.APPOINTMENT_ALREADY_BOOKED

    def __init__(self, message: str = AppointmentMessages.SLOT_UNAVAILABLE):
        super().__init__(message)


class AppointmentNotFoundException(ResourceNotFoundException):

    error_code = ErrorCode.APPOINTMENT_NOT_FOUND

    def __init__(self, message: str = "Appointment not found"):
        super().__init__(message)


class AppointmentOwnershipException(ForbiddenAccessException):

    error_code = ErrorCode.FORBIDDEN_ACCESS

    def __init__(self, message: str = AppointmentMessages.APPOINTMENT_OWNERSHIP_VIOLATION):
        super().__init__(message)


class AppointmentNotCancellableException(InvalidOperationException):

    error_code = ErrorCode.APPOINTMENT_NOT_CANCELLABLE

    def __init__(self, message: str = AppointmentMessages.NOT_CANCELLABLE):
        super().__init__(message)


class CancellationWindowPassedException(InvalidOperationException):

    error_code = ErrorCode.CANCELLATION_WINDOW_PASSED

    def __init__(self, hours: int):
        message = AppointmentMessages.CANCELLATION_WINDOW_PASSED.format(hours=hours)
        super().__init__(message)


class AppointmentNotCompletableException(InvalidOperationException):

    error_code = ErrorCode.APPOINTMENT_NOT_COMPLETABLE

    def __init__(self, message: str = AppointmentMessages.NOT_COMPLETABLE):
        super().__init__(message)


class AppointmentTimeNotPassedException(InvalidOperationException):

    error_code = ErrorCode.APPOINTMENT_TIME_NOT_PASSED

    def __init__(self, message: str = AppointmentMessages.TIME_NOT_PASSED):
        super().__init__(message)