"""
This is the dependency every protected route will use to find out who is
calling it.

It reads the "Authorization: Bearer <token>" header, decodes the JWT, and
hands the route a small CurrentUser object with just the fields we need
(id, email, role). There is no database call here - the JWT
already carries everything we need, so checking who someone is stays
fast even as the project grows into more services later.
"""

import jwt
from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from pydantic import BaseModel

from user_service.constants.roles import Role
from user_service.utils.jwt_handler import decode_access_token

# This makes Swagger show the little "Authorize" lock icon and lets us
# pull the raw Bearer token out of the Authorization header.
bearer_scheme = HTTPBearer()


class CurrentUser(BaseModel):
    """
    A lightweight stand-in for "the logged in user", built straight from
    the JWT claims instead of a database lookup.
    """

    id: str
    email: str
    role: Role


async def get_current_user(
    credentials: HTTPAuthorizationCredentials = Depends(bearer_scheme),
) -> CurrentUser:
    """
    Dependency to attach to any route that requires a logged in user.

    Usage:
        @router.get("/something")
        async def something(current_user: CurrentUser = Depends(get_current_user)):
            ...
    """
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
    Role based access control dependency.

    Usage:
        @router.get("/admin-only-thing")
        async def admin_only_thing(
            current_user: CurrentUser = Depends(require_role(Role.ADMIN)),
        ):
            ...
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