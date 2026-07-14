import jwt
import pytest
from fastapi.security import HTTPAuthorizationCredentials

from backend.constants.roles import Role
from backend.exceptions.custom_exceptions import (
    InsufficientRoleException,
    InvalidTokenException,
    TokenExpiredException,
)
from backend.middleware.auth import CurrentUser, get_current_user, require_role


def _credentials(token: str = "some-token") -> HTTPAuthorizationCredentials:
    return HTTPAuthorizationCredentials(scheme="Bearer", credentials=token)


async def test_get_current_user_returns_current_user_for_a_valid_token(mocker):
    mocker.patch(
        "backend.middleware.auth.decode_access_token",
        return_value={"sub": "user-1", "email": "user@example.com", "role": "PATIENT"},
    )

    current_user = await get_current_user(credentials=_credentials())

    assert isinstance(current_user, CurrentUser)
    assert current_user.id == "user-1"
    assert current_user.email == "user@example.com"
    assert current_user.role == Role.PATIENT


async def test_get_current_user_raises_token_expired_for_an_expired_token(mocker):
    mocker.patch(
        "backend.middleware.auth.decode_access_token",
        side_effect=jwt.ExpiredSignatureError(),
    )

    with pytest.raises(TokenExpiredException):
        await get_current_user(credentials=_credentials())


async def test_get_current_user_raises_invalid_token_for_a_malformed_token(mocker):
    mocker.patch(
        "backend.middleware.auth.decode_access_token",
        side_effect=jwt.InvalidTokenError(),
    )

    with pytest.raises(InvalidTokenException):
        await get_current_user(credentials=_credentials())


async def test_require_role_allows_a_user_with_an_allowed_role():
    checker = require_role(Role.ADMIN, Role.DOCTOR)
    current_user = CurrentUser(id="1", email="doc@example.com", role=Role.DOCTOR)

    result = await checker(current_user=current_user)

    assert result == current_user


async def test_require_role_rejects_a_user_without_an_allowed_role():
    checker = require_role(Role.ADMIN)
    current_user = CurrentUser(id="1", email="patient@example.com", role=Role.PATIENT)

    with pytest.raises(InsufficientRoleException):
        await checker(current_user=current_user)
