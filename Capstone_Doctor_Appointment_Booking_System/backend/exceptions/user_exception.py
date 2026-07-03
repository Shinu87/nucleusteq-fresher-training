"""
Exceptions raised for the User model / patient registration flow.
"""

from backend.constants.error_constants import ErrorCode
from backend.constants.message_constants import UserMessages
from backend.exceptions.base_exception import DuplicateResourceException, ResourceNotFoundException


class UserNotFoundException(ResourceNotFoundException):

    error_code = ErrorCode.USER_NOT_FOUND

    def __init__(self, message: str = UserMessages.USER_NOT_FOUND):
        super().__init__(message)


class EmailAlreadyRegisteredException(DuplicateResourceException):

    error_code = ErrorCode.EMAIL_ALREADY_REGISTERED

    def __init__(self, message: str = UserMessages.EMAIL_ALREADY_REGISTERED):
        super().__init__(message)
