"""
Appointment Service never ISSUES a JWT - only User Service does that at
login. This service needs is to DECODE a token someone sends it and
check it is valid, using the same JWT_SECRET_KEY that's also set in
user_service/.env. This is the "shared JWT validation" strategy.
"""

import jwt

from appointment_service.config import get_settings

settings = get_settings()


def decode_access_token(token: str) -> dict:
    """
    Decodes a JWT and returns its claims.

    Raises:
        jwt.ExpiredSignatureError: the token's exp time has passed.
        jwt.InvalidTokenError: anything else wrong with the token
            (bad signature, malformed token, wrong algorithm, etc).
    """
    return jwt.decode(token, settings.jwt_secret_key, algorithms=[settings.jwt_algorithm])