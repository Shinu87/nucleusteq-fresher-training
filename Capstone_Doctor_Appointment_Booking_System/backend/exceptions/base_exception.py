"""
Root of the custom exception hierarchy.
"""

from fastapi import status

from backend.constants.common_errors import CommonErrorCode


class AppException(Exception):
    """
    Base class for every custom exception raised by this application.
    """

    status_code: int = status.HTTP_500_INTERNAL_SERVER_ERROR
    error_code: str = CommonErrorCode.INTERNAL_SERVER_ERROR
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
    error_code = CommonErrorCode.RESOURCE_NOT_FOUND


class DuplicateResourceException(AppException):

    status_code = status.HTTP_400_BAD_REQUEST
    error_code = CommonErrorCode.DUPLICATE_RESOURCE


class InvalidOperationException(AppException):

    status_code = status.HTTP_400_BAD_REQUEST
    error_code = CommonErrorCode.INVALID_OPERATION


class UnauthorizedAccessException(AppException):

    status_code = status.HTTP_401_UNAUTHORIZED
    error_code = CommonErrorCode.UNAUTHORIZED_ACCESS


class ForbiddenAccessException(AppException):

    status_code = status.HTTP_403_FORBIDDEN
    error_code = CommonErrorCode.FORBIDDEN_ACCESS
