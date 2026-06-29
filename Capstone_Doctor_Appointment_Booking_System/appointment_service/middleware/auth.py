"""
Authentication helper for Appointment Service.

This reads the JWT token to find out which user is sending the request.
Since all services use the same secret key, the token can be verified
locally without asking User Service every time.

Even though the logic is similar to User Service, it is implemented
again here because each microservice should be independent and should
not directly share code with another service.
"""

import jwt
from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from pydantic import BaseModel

from appointment_service.constants.roles import Role
from appointment_service.utils.jwt_handler import decode_access_token

bearer_scheme = HTTPBearer()


class CurrentUser(BaseModel):
    """A lightweight stand-in for "the logged in user", built from the JWT claims."""

    id: str
    email: str
    role: Role


async def get_current_user(
    credentials: HTTPAuthorizationCredentials = Depends(bearer_scheme),
) -> CurrentUser:
    token = credentials.credentials

    try:
        payload = decode_access_token(token)
    except jwt.ExpiredSignatureError:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Your session has expired. Please log in again.",
            headers={"WWW-Authenticate": "Bearer"},
        )
    except jwt.InvalidTokenError:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid authentication token.",
            headers={"WWW-Authenticate": "Bearer"},
        )

    return CurrentUser(
        id=payload["sub"],
        email=payload["email"],
        role=payload["role"],
    )


def require_role(*allowed_roles: Role):
    """
    Dependency factory - same pattern as User Service's require_role.
    Call it with the roles allowed for a route, e.g.
    Depends(require_role(Role.DOCTOR)).
    """

    async def role_checker(
        current_user: CurrentUser = Depends(get_current_user),
    ) -> CurrentUser:
        if current_user.role not in allowed_roles:
            allowed_names = ", ".join(role.value for role in allowed_roles)
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=f"This action requires one of these roles: {allowed_names}",
            )
        return current_user

    return role_checker