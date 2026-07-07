"""
Exceptions raised for the doctor profile / application / approval
workflow, and for the doctor search model.
"""

from backend.constants.doctor_errors import DoctorErrorCode
from backend.constants.doctor_messages import DoctorMessages
from backend.exceptions.base_exception import (
    DuplicateResourceException,
    InvalidOperationException,
    ResourceNotFoundException,
)


class DoctorNotFoundException(ResourceNotFoundException):

    error_code = DoctorErrorCode.DOCTOR_NOT_FOUND

    def __init__(self, message: str = DoctorMessages.DOCTOR_NOT_FOUND):
        super().__init__(message)


class DoctorProfileSyncMissingException(ResourceNotFoundException):

    error_code = DoctorErrorCode.DOCTOR_PROFILE_SYNC_MISSING

    def __init__(self, message: str = DoctorMessages.DOCTOR_PROFILE_SYNC_MISSING):
        super().__init__(message)


class DoctorApplicationNotFoundException(ResourceNotFoundException):

    error_code = DoctorErrorCode.DOCTOR_APPLICATION_NOT_FOUND

    def __init__(self, message: str = DoctorMessages.APPLICATION_NOT_FOUND):
        super().__init__(message)


class DuplicateLicenseException(DuplicateResourceException):

    error_code = DoctorErrorCode.DUPLICATE_LICENSE

    def __init__(self, message: str = DoctorMessages.LICENSE_ALREADY_REGISTERED):
        super().__init__(message)


class ApplicationAlreadyReviewedException(InvalidOperationException):

    error_code = DoctorErrorCode.APPLICATION_ALREADY_REVIEWED

    def __init__(self, status_label: str):
        super().__init__(DoctorMessages.APPLICATION_ALREADY_REVIEWED.format(status=status_label))


class DoctorNotApprovedException(InvalidOperationException):

    error_code = DoctorErrorCode.DOCTOR_NOT_APPROVED

    def __init__(self, message: str = "This doctor's application has not been approved yet."):
        super().__init__(message)
