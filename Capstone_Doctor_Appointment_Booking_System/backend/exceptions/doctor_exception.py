"""
Exceptions raised for the doctor profile / application / approval
workflow, and for the doctor search model.
"""

from backend.constants.error_constants import ErrorCode
from backend.constants.message_constants import DoctorMessages
from backend.exceptions.base_exception import (
    DuplicateResourceException,
    InvalidOperationException,
    ResourceNotFoundException,
)


class DoctorNotFoundException(ResourceNotFoundException):

    error_code = ErrorCode.DOCTOR_NOT_FOUND

    def __init__(self, message: str = DoctorMessages.DOCTOR_NOT_FOUND):
        super().__init__(message)


class DoctorProfileSyncMissingException(ResourceNotFoundException):

    error_code = ErrorCode.DOCTOR_PROFILE_SYNC_MISSING

    def __init__(self, message: str = DoctorMessages.DOCTOR_PROFILE_SYNC_MISSING):
        super().__init__(message)


class DoctorApplicationNotFoundException(ResourceNotFoundException):

    error_code = ErrorCode.DOCTOR_APPLICATION_NOT_FOUND

    def __init__(self, message: str = DoctorMessages.APPLICATION_NOT_FOUND):
        super().__init__(message)


class DuplicateLicenseException(DuplicateResourceException):

    error_code = ErrorCode.DUPLICATE_LICENSE

    def __init__(self, message: str = DoctorMessages.LICENSE_ALREADY_REGISTERED):
        super().__init__(message)


class ApplicationAlreadyReviewedException(InvalidOperationException):

    error_code = ErrorCode.APPLICATION_ALREADY_REVIEWED

    def __init__(self, status_label: str):
        super().__init__(DoctorMessages.APPLICATION_ALREADY_REVIEWED.format(status=status_label))


class DoctorNotApprovedException(InvalidOperationException):

    error_code = ErrorCode.DOCTOR_NOT_APPROVED

    def __init__(self, message: str = "This doctor's application has not been approved yet."):
        super().__init__(message)
