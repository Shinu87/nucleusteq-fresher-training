"""
Helpers for creating and decoding our JWT access tokens.

Every service in this project (User, Appointment, Notification) is going
to validate tokens using this exact same pattern once they exist, because
they all share the same JWT_SECRET_KEY.
"""

from datetime import datetime, timedelta, timezone

import jwt

from backend.config import get_settings
from backend.models.user import User

settings = get_settings()


def _utc_now() -> datetime:
    return datetime.now(timezone.utc)


def create_access_token(user: User) -> str:
    """
    Builds the JWT we hand back to the frontend after a successful login.
    """
    issued_at = _utc_now()
    expires_at = issued_at + timedelta(minutes=settings.jwt_expire_minutes)

    payload = {
        "sub": str(user.id),
        "email": user.email,
        "role": user.role.value,
        "iat": issued_at,
        "exp": expires_at,
    }

    return jwt.encode(payload, settings.jwt_secret_key, algorithm=settings.jwt_algorithm)


def decode_access_token(token: str) -> dict:
    """
    Decodes a JWT and returns its claims.

    Raises:
        jwt.ExpiredSignatureError: the token's exp time has passed.
        jwt.InvalidTokenError: anything else wrong with the token
            (bad signature, malformed token, wrong algorithm, etc).

    The caller (our auth middleware) turns these exceptions into proper
    401 responses - this function only deals with the token itself.
    """
    return jwt.decode(token, settings.jwt_secret_key, algorithms=[settings.jwt_algorithm])