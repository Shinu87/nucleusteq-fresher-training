"""
Exceptions raised for the User model / patient registration flow.
"""

from backend.constants.user_errors import UserErrorCode
from backend.constants.user_messages import UserMessages
from backend.exceptions.base_exception import DuplicateResourceException, ResourceNotFoundException


class UserNotFoundException(ResourceNotFoundException):

    error_code = UserErrorCode.USER_NOT_FOUND

    def __init__(self, message: str = UserMessages.USER_NOT_FOUND):
        super().__init__(message)


class EmailAlreadyRegisteredException(DuplicateResourceException):

    error_code = UserErrorCode.EMAIL_ALREADY_REGISTERED

    def __init__(self, message: str = UserMessages.EMAIL_ALREADY_REGISTERED):
        super().__init__(message)
