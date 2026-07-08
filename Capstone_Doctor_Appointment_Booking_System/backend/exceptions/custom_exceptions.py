from backend.constants.slot_errors import (
    DUPLICATE_SLOT as DUPLICATE_SLOT_CODE,
    NO_SLOTS_GENERATED as NO_SLOTS_GENERATED_CODE,
    SLOT_IN_PAST,
    SLOT_NOT_AVAILABLE,
    SLOT_NOT_DELETABLE as SLOT_NOT_DELETABLE_CODE,
    SLOT_NOT_EDITABLE as SLOT_NOT_EDITABLE_CODE,
    SLOT_NOT_FOUND as SLOT_NOT_FOUND_CODE,
)
from backend.constants.slot_messages import (
    DUPLICATE_SLOT as DUPLICATE_SLOT_MESSAGE,
    INVALID_TIME_RANGE,
    NO_SLOTS_GENERATED as NO_SLOTS_GENERATED_MESSAGE,
    SLOT_NOT_DELETABLE as SLOT_NOT_DELETABLE_MESSAGE,
    SLOT_NOT_EDITABLE as SLOT_NOT_EDITABLE_MESSAGE,
    SLOT_NOT_FOUND as SLOT_NOT_FOUND_MESSAGE,
    SLOT_OWNERSHIP_VIOLATION,
)

# Availability slot exceptions

class SlotNotFoundException(ResourceNotFoundException):
    error_code = SLOT_NOT_FOUND_CODE

    def __init__(self, message: str = SLOT_NOT_FOUND_MESSAGE):
        super().__init__(message)


class SlotOwnershipException(ForbiddenAccessException):
    error_code = FORBIDDEN_ACCESS

    def __init__(self, message: str = SLOT_OWNERSHIP_VIOLATION):
        super().__init__(message)


class SlotNotEditableException(InvalidOperationException):
    error_code = SLOT_NOT_EDITABLE_CODE

    def __init__(self, message: str = SLOT_NOT_EDITABLE_MESSAGE):
        super().__init__(message)


class SlotNotDeletableException(InvalidOperationException):
    error_code = SLOT_NOT_DELETABLE_CODE

    def __init__(self, message: str = SLOT_NOT_DELETABLE_MESSAGE):
        super().__init__(message)


class InvalidSlotTimeRangeException(InvalidOperationException):
    error_code = INVALID_OPERATION

    def __init__(self, message: str = INVALID_TIME_RANGE):
        super().__init__(message)


class DuplicateSlotException(DuplicateResourceException):
    error_code = DUPLICATE_SLOT_CODE

    def __init__(self, message: str = DUPLICATE_SLOT_MESSAGE):
        super().__init__(message)


class NoSlotsGeneratedException(InvalidOperationException):
    error_code = NO_SLOTS_GENERATED_CODE

    def __init__(self, message: str = NO_SLOTS_GENERATED_MESSAGE):
        super().__init__(message)


class SlotNotAvailableException(AppException):
    status_code = status.HTTP_409_CONFLICT
    error_code = SLOT_NOT_AVAILABLE

    def __init__(self, message: str = SLOT_UNAVAILABLE):
        super().__init__(message)

