"""
Exception for manual validation failures raised outside of Pydantic's
own request-body validation.
"""

from fastapi import status

from backend.constants.common_errors import CommonErrorCode
from backend.constants.common_messages import GeneralMessages
from backend.exceptions.base_exception import AppException


class InvalidInputException(AppException):

    status_code = status.HTTP_422_UNPROCESSABLE_CONTENT
    error_code = CommonErrorCode.VALIDATION_ERROR

    def __init__(self, message: str = GeneralMessages.VALIDATION_FAILED):
        super().__init__(message)
