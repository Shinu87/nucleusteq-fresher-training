"""
All custom exceptions raised across the application, consolidated into
a single file.
"""

from fastapi import status

from backend.constants.auth_errors import (
    ACCOUNT_INACTIVE,
    ACCOUNT_PENDING_APPROVAL,
    ACCOUNT_REJECTED,
    FORBIDDEN_ACCESS as AUTH_FORBIDDEN_ACCESS,
    INVALID_CREDENTIALS as INVALID_CREDENTIALS_CODE,
    INVALID_SETUP_TOKEN,
    INVALID_TOKEN as INVALID_TOKEN_CODE,
    SETUP_TOKEN_EXPIRED,
    TOKEN_EXPIRED as TOKEN_EXPIRED_CODE,
)
from backend.constants.auth_messages import (
    ACCOUNT_INACTIVE as ACCOUNT_INACTIVE_MESSAGE,
    ACCOUNT_PENDING_APPROVAL as ACCOUNT_PENDING_APPROVAL_MESSAGE,
    ACCOUNT_REJECTED as ACCOUNT_REJECTED_MESSAGE,
    INSUFFICIENT_ROLE,
    INVALID_CREDENTIALS as INVALID_CREDENTIALS_MESSAGE,
    INVALID_TOKEN as INVALID_TOKEN_MESSAGE,
    SETUP_LINK_EXPIRED,
    SETUP_LINK_INVALID,
    TOKEN_EXPIRED as TOKEN_EXPIRED_MESSAGE,
)
from backend.constants.common_errors import (
    DUPLICATE_RESOURCE,
    FORBIDDEN_ACCESS,
    INTERNAL_SERVER_ERROR,
    INVALID_OPERATION,
    RESOURCE_NOT_FOUND,
    UNAUTHORIZED_ACCESS,
    VALIDATION_ERROR,
)
from backend.constants.user_errors import (
    EMAIL_ALREADY_REGISTERED as EMAIL_ALREADY_REGISTERED_CODE,
    USER_NOT_FOUND as USER_NOT_FOUND_CODE,
)
from backend.constants.user_messages import (
    EMAIL_ALREADY_REGISTERED as EMAIL_ALREADY_REGISTERED_MESSAGE,
    USER_NOT_FOUND as USER_NOT_FOUND_MESSAGE,
)

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

# Base exception hierarchy


class AppException(Exception):

    status_code: int = status.HTTP_500_INTERNAL_SERVER_ERROR
    error_code: str = INTERNAL_SERVER_ERROR
    headers: dict | None = None

    def __init__(
        self,
        message: str,
        *,
        status_code: int | None = None,
        error_code: str | None = None,
        headers: dict | None = None,
    ):
        self.message = message
        if status_code is not None:
            self.status_code = status_code
        if error_code is not None:
            self.error_code = error_code
        if headers is not None:
            self.headers = headers
        super().__init__(self.message)


class ResourceNotFoundException(AppException):

    status_code = status.HTTP_404_NOT_FOUND
    error_code = RESOURCE_NOT_FOUND


class DuplicateResourceException(AppException):

    status_code = status.HTTP_400_BAD_REQUEST
    error_code = DUPLICATE_RESOURCE


class InvalidOperationException(AppException):

    status_code = status.HTTP_400_BAD_REQUEST
    error_code = INVALID_OPERATION


class UnauthorizedAccessException(AppException):

    status_code = status.HTTP_401_UNAUTHORIZED
    error_code = UNAUTHORIZED_ACCESS


class ForbiddenAccessException(AppException):

    status_code = status.HTTP_403_FORBIDDEN
    error_code = FORBIDDEN_ACCESS


# Auth exceptions


class InvalidCredentialsException(UnauthorizedAccessException):

    error_code = INVALID_CREDENTIALS_CODE

    def __init__(self, message: str = INVALID_CREDENTIALS_MESSAGE):
        super().__init__(message)


class AccountPendingApprovalException(UnauthorizedAccessException):

    error_code = ACCOUNT_PENDING_APPROVAL

    def __init__(self, message: str = ACCOUNT_PENDING_APPROVAL_MESSAGE):
        super().__init__(message)


class AccountRejectedException(UnauthorizedAccessException):

    error_code = ACCOUNT_REJECTED

    def __init__(self, message: str = ACCOUNT_REJECTED_MESSAGE):
        super().__init__(message)


class AccountInactiveException(UnauthorizedAccessException):

    error_code = ACCOUNT_INACTIVE

    def __init__(self, message: str = ACCOUNT_INACTIVE_MESSAGE):
        super().__init__(message)


class TokenExpiredException(UnauthorizedAccessException):

    error_code = TOKEN_EXPIRED_CODE

    def __init__(self, message: str = TOKEN_EXPIRED_MESSAGE):
        super().__init__(message, headers={"WWW-Authenticate": "Bearer"})


class InvalidTokenException(UnauthorizedAccessException):

    error_code = INVALID_TOKEN_CODE

    def __init__(self, message: str = INVALID_TOKEN_MESSAGE):
        super().__init__(message, headers={"WWW-Authenticate": "Bearer"})


class InvalidSetupTokenException(InvalidOperationException):

    error_code = INVALID_SETUP_TOKEN

    def __init__(self, message: str = SETUP_LINK_INVALID):
        super().__init__(message)


class SetupTokenExpiredException(InvalidOperationException):

    error_code = SETUP_TOKEN_EXPIRED

    def __init__(self, message: str = SETUP_LINK_EXPIRED):
        super().__init__(message)


class InsufficientRoleException(ForbiddenAccessException):

    error_code = AUTH_FORBIDDEN_ACCESS

    def __init__(self, allowed_roles: str):
        super().__init__(INSUFFICIENT_ROLE.format(roles=allowed_roles))


# User exceptions

class UserNotFoundException(ResourceNotFoundException):

    error_code = USER_NOT_FOUND_CODE

    def __init__(self, message: str = USER_NOT_FOUND_MESSAGE):
        super().__init__(message)


class EmailAlreadyRegisteredException(DuplicateResourceException):

    error_code = EMAIL_ALREADY_REGISTERED_CODE

    def __init__(self, message: str = EMAIL_ALREADY_REGISTERED_MESSAGE):
        super().__init__(message)
        
        

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


