"""
Exceptions raised by the authentication / login / JWT / setup-token flow.
"""

from backend.constants.auth_errors import AuthErrorCode
from backend.constants.auth_messages import AuthMessages
from backend.exceptions.base_exception import (
    ForbiddenAccessException,
    InvalidOperationException,
    UnauthorizedAccessException,
)


class InvalidCredentialsException(UnauthorizedAccessException):

    error_code = AuthErrorCode.INVALID_CREDENTIALS

    def __init__(self, message: str = AuthMessages.INVALID_CREDENTIALS):
        super().__init__(message)


class AccountPendingApprovalException(UnauthorizedAccessException):

    error_code = AuthErrorCode.ACCOUNT_PENDING_APPROVAL

    def __init__(self, message: str = AuthMessages.ACCOUNT_PENDING_APPROVAL):
        super().__init__(message)


class AccountRejectedException(UnauthorizedAccessException):

    error_code = AuthErrorCode.ACCOUNT_REJECTED

    def __init__(self, message: str = AuthMessages.ACCOUNT_REJECTED):
        super().__init__(message)


class AccountInactiveException(UnauthorizedAccessException):

    error_code = AuthErrorCode.ACCOUNT_INACTIVE

    def __init__(self, message: str = AuthMessages.ACCOUNT_INACTIVE):
        super().__init__(message)


class TokenExpiredException(UnauthorizedAccessException):

    error_code = AuthErrorCode.TOKEN_EXPIRED

    def __init__(self, message: str = AuthMessages.TOKEN_EXPIRED):
        super().__init__(message, headers={"WWW-Authenticate": "Bearer"})


class InvalidTokenException(UnauthorizedAccessException):

    error_code = AuthErrorCode.INVALID_TOKEN

    def __init__(self, message: str = AuthMessages.INVALID_TOKEN):
        super().__init__(message, headers={"WWW-Authenticate": "Bearer"})


class InvalidSetupTokenException(InvalidOperationException):

    error_code = AuthErrorCode.INVALID_SETUP_TOKEN

    def __init__(self, message: str = AuthMessages.SETUP_LINK_INVALID):
        super().__init__(message)


class SetupTokenExpiredException(InvalidOperationException):

    error_code = AuthErrorCode.SETUP_TOKEN_EXPIRED

    def __init__(self, message: str = AuthMessages.SETUP_LINK_EXPIRED):
        super().__init__(message)


class InsufficientRoleException(ForbiddenAccessException):

    error_code = AuthErrorCode.FORBIDDEN_ACCESS

    def __init__(self, allowed_roles: str):
        super().__init__(AuthMessages.INSUFFICIENT_ROLE.format(roles=allowed_roles))
