from datetime import datetime, timedelta, timezone

import jwt
import pytest
from beanie import PydanticObjectId

from backend.constants.roles import Role
from backend.models.user import User
from backend.utils.jwt_handler import create_access_token, decode_access_token
from backend.config import get_settings

settings = get_settings()


def _make_user() -> User:
    return User(
        id=PydanticObjectId(),
        full_name="Test Patient",
        email="patient@example.com",
        password_hash="irrelevant-hash",
        phone_number="9876543210",
        role=Role.PATIENT,
    )


def test_create_access_token_returns_a_decodable_jwt():
    user = _make_user()

    token = create_access_token(user)

    payload = jwt.decode(token, settings.jwt_secret_key, algorithms=[settings.jwt_algorithm])
    assert payload["sub"] == str(user.id)
    assert payload["email"] == user.email
    assert payload["role"] == Role.PATIENT.value


def test_decode_access_token_returns_the_same_claims_that_were_encoded():
    user = _make_user()
    token = create_access_token(user)

    claims = decode_access_token(token)

    assert claims["sub"] == str(user.id)
    assert claims["email"] == user.email
    assert claims["role"] == Role.PATIENT.value


def test_decode_access_token_raises_for_an_expired_token():
    user = _make_user()
    issued_at = datetime.now(timezone.utc) - timedelta(minutes=60)
    expired_payload = {
        "sub": str(user.id),
        "email": user.email,
        "role": user.role.value,
        "iat": issued_at,
        "exp": issued_at + timedelta(minutes=1),
    }
    expired_token = jwt.encode(
        expired_payload, settings.jwt_secret_key, algorithm=settings.jwt_algorithm
    )

    with pytest.raises(jwt.ExpiredSignatureError):
        decode_access_token(expired_token)


def test_decode_access_token_raises_for_a_malformed_token():
    with pytest.raises(jwt.InvalidTokenError):
        decode_access_token("this-is-not-a-valid-jwt")


def test_decode_access_token_raises_for_a_token_signed_with_a_different_secret():
    user = _make_user()
    wrong_secret_token = jwt.encode(
        {
            "sub": str(user.id),
            "email": user.email,
            "role": user.role.value,
            "exp": datetime.now(timezone.utc) + timedelta(minutes=30),
        },
        "a-completely-different-secret",
        algorithm=settings.jwt_algorithm,
    )

    with pytest.raises(jwt.InvalidTokenError):
        decode_access_token(wrong_secret_token)
