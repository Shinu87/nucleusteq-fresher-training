"""
Exceptions raised for availability slot management.
"""

from fastapi import status

from backend.constants.slot_errors import SlotErrorCode
from backend.constants.slot_messages import SlotMessages
from backend.constants.appointment_messages import AppointmentMessages
from backend.constants.common_errors import CommonErrorCode
from backend.exceptions.base_exception import (
    AppException,
    DuplicateResourceException,
    ForbiddenAccessException,
    InvalidOperationException,
    ResourceNotFoundException,
)


class SlotNotFoundException(ResourceNotFoundException):
    error_code = SlotErrorCode.SLOT_NOT_FOUND

    def __init__(self, message: str = SlotMessages.SLOT_NOT_FOUND):
        super().__init__(message)


class SlotOwnershipException(ForbiddenAccessException):
    error_code = CommonErrorCode.FORBIDDEN_ACCESS

    def __init__(self, message: str = SlotMessages.SLOT_OWNERSHIP_VIOLATION):
        super().__init__(message)


class SlotNotEditableException(InvalidOperationException):
    error_code = SlotErrorCode.SLOT_NOT_EDITABLE

    def __init__(self, message: str = SlotMessages.SLOT_NOT_EDITABLE):
        super().__init__(message)


class SlotNotDeletableException(InvalidOperationException):
    error_code = SlotErrorCode.SLOT_NOT_DELETABLE

    def __init__(self, message: str = SlotMessages.SLOT_NOT_DELETABLE):
        super().__init__(message)


class InvalidSlotTimeRangeException(InvalidOperationException):
    error_code = CommonErrorCode.INVALID_OPERATION

    def __init__(self, message: str = SlotMessages.INVALID_TIME_RANGE):
        super().__init__(message)


class DuplicateSlotException(DuplicateResourceException):
    error_code = SlotErrorCode.DUPLICATE_SLOT

    def __init__(self, message: str = SlotMessages.DUPLICATE_SLOT):
        super().__init__(message)


class NoSlotsGeneratedException(InvalidOperationException):
    error_code = SlotErrorCode.NO_SLOTS_GENERATED

    def __init__(self, message: str = SlotMessages.NO_SLOTS_GENERATED):
        super().__init__(message)


class SlotNotAvailableException(AppException):
    status_code = status.HTTP_409_CONFLICT
    error_code = SlotErrorCode.SLOT_NOT_AVAILABLE

    def __init__(self, message: str = AppointmentMessages.SLOT_UNAVAILABLE):
        super().__init__(message)