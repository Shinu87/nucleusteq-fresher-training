"""
Helpers for creating and decoding our JWT access tokens.
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
    Builds the JWT token.
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
    Decodes a JWT.
    """
    return jwt.decode(token, settings.jwt_secret_key, algorithms=[settings.jwt_algorithm])