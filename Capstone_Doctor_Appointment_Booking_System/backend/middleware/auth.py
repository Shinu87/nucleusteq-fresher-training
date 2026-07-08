"""
Authentication dependency for protected routes.
"""

import jwt
from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from pydantic import BaseModel

from backend.constants.roles import Role
from backend.utils.jwt_handler import decode_access_token
from backend.exceptions.custom_exceptions import InsufficientRoleException, InvalidTokenException, TokenExpiredException

bearer_scheme = HTTPBearer()


class CurrentUser(BaseModel):

    id: str
    email: str
    role: Role


async def get_current_user(
    credentials: HTTPAuthorizationCredentials = Depends(bearer_scheme),
) -> CurrentUser:
    """
    Dependency to attach to any route that requires a logged in user.
    """
    token = credentials.credentials

    try:
        payload = decode_access_token(token)
    except jwt.ExpiredSignatureError:
        raise TokenExpiredException()
    except jwt.InvalidTokenError:
        raise InvalidTokenException()

    return CurrentUser(
        id=payload["sub"],
        email=payload["email"],
        role=payload["role"],
    )

def require_role(*allowed_roles: Role):
    """
    Role based access control dependency.
    """

    async def role_checker(
        current_user: CurrentUser = Depends(get_current_user),
    ) -> CurrentUser:
        if current_user.role not in allowed_roles:
            allowed_names = ", ".join(role.value for role in allowed_roles)
            raise InsufficientRoleException(allowed_names)
        return current_user

    return role_checker