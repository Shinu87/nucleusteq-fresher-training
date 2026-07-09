
from backend.constants.doctor_errors import (
    APPLICATION_ALREADY_REVIEWED as APPLICATION_ALREADY_REVIEWED_CODE,
    DOCTOR_APPLICATION_NOT_FOUND,
    DOCTOR_NOT_APPROVED,
    DOCTOR_NOT_FOUND as DOCTOR_NOT_FOUND_CODE,
    DOCTOR_PROFILE_SYNC_MISSING as DOCTOR_PROFILE_SYNC_MISSING_CODE,
    DUPLICATE_LICENSE,
)

from backend.constants.doctor_messages import (
    APPLICATION_ALREADY_REVIEWED as APPLICATION_ALREADY_REVIEWED_MESSAGE,
    APPLICATION_NOT_FOUND,
    DOCTOR_NOT_FOUND as DOCTOR_NOT_FOUND_MESSAGE,
    DOCTOR_PROFILE_SYNC_MISSING as DOCTOR_PROFILE_SYNC_MISSING_MESSAGE,
    LICENSE_ALREADY_REGISTERED,
)

# Doctor exceptions

class DoctorNotFoundException(ResourceNotFoundException):

    error_code = DOCTOR_NOT_FOUND_CODE

    def __init__(self, message: str = DOCTOR_NOT_FOUND_MESSAGE):
        super().__init__(message)


class DoctorProfileSyncMissingException(ResourceNotFoundException):

    error_code = DOCTOR_PROFILE_SYNC_MISSING_CODE

    def __init__(self, message: str = DOCTOR_PROFILE_SYNC_MISSING_MESSAGE):
        super().__init__(message)


class DoctorApplicationNotFoundException(ResourceNotFoundException):

    error_code = DOCTOR_APPLICATION_NOT_FOUND

    def __init__(self, message: str = APPLICATION_NOT_FOUND):
        super().__init__(message)


class DuplicateLicenseException(DuplicateResourceException):

    error_code = DUPLICATE_LICENSE

    def __init__(self, message: str = LICENSE_ALREADY_REGISTERED):
        super().__init__(message)


class ApplicationAlreadyReviewedException(InvalidOperationException):

    error_code = APPLICATION_ALREADY_REVIEWED_CODE

    def __init__(self, status_label: str):
        super().__init__(APPLICATION_ALREADY_REVIEWED_MESSAGE.format(status=status_label))


class DoctorNotApprovedException(InvalidOperationException):

    error_code = DOCTOR_NOT_APPROVED

    def __init__(self, message: str = DOCTOR_NOT_APPROVED):
        super().__init__(message)
