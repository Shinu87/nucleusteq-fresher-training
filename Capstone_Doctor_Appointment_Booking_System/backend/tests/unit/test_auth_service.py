from datetime import date, datetime, timedelta, timezone

import pytest
from beanie import PydanticObjectId
from unittest.mock import AsyncMock, MagicMock

from backend.constants.account_status import AccountStatus
from backend.constants.approval_status import ApprovalStatus
from backend.constants.gender import Gender
from backend.constants.roles import Role
from backend.exceptions.custom_exceptions import (
    AccountInactiveException,
    EmailAlreadyRegisteredException,
    InvalidCredentialsException,
    InvalidSetupTokenException,
    SetupTokenExpiredException,
    UserNotFoundException,
)
from backend.constants.specialization import Specialization
from backend.models.doctor_profile import DoctorProfile
from backend.models.user import User
from backend.schemas.request.auth_request import LoginRequest, PatientRegisterRequest
from backend.services.auth_service import AuthService
from backend.utils.security import hash_password


@pytest.fixture
def user_repository():
    return AsyncMock()


@pytest.fixture
def doctor_profile_repository():
    return AsyncMock()


@pytest.fixture
def doctor_sync_service():
    return AsyncMock()


@pytest.fixture
def auth_service(user_repository, doctor_profile_repository, doctor_sync_service):
    return AuthService(
        user_repository=user_repository,
        doctor_profile_repository=doctor_profile_repository,
        doctor_sync_service=doctor_sync_service,
    )


def _make_patient_payload() -> PatientRegisterRequest:
    return PatientRegisterRequest(
        full_name="Atharv Gokhale",
        email="atharv.gokhale@example.com",
        phone_number="9876543210",
        password="Secret@123",
        gender=Gender.MALE,
        date_of_birth=date(1995, 5, 20),
    )


def _make_user(**overrides) -> User:
    defaults = dict(
        id=PydanticObjectId(),
        full_name="Atharv Gokhale",
        email="atharv.gokhale@example.com",
        password_hash=hash_password("Secret@123"),
        phone_number="9876543210",
        role=Role.PATIENT,
        account_status=AccountStatus.ACTIVE,
    )
    defaults.update(overrides)
    return User(**defaults)


# register_patient


async def test_register_patient_success(auth_service, user_repository):
    user_repository.find_by_email.return_value = None
    user_repository.insert.side_effect = lambda user: user 

    payload = _make_patient_payload()
    new_user = await auth_service.register_patient(payload)

    assert new_user.email == payload.email
    assert new_user.role == Role.PATIENT
    assert new_user.password_hash != payload.password
    user_repository.insert.assert_awaited_once()


async def test_register_patient_raises_when_email_already_registered(auth_service, user_repository):
    user_repository.find_by_email.return_value = _make_user()

    with pytest.raises(EmailAlreadyRegisteredException):
        await auth_service.register_patient(_make_patient_payload())

    user_repository.insert.assert_not_awaited()


# authenticate_user


async def test_authenticate_user_success(auth_service, user_repository):
    existing_user = _make_user()
    user_repository.find_by_email.return_value = existing_user

    result = await auth_service.authenticate_user(
        LoginRequest(email=existing_user.email, password="Secret@123")
    )

    assert result == existing_user


async def test_authenticate_user_raises_for_unknown_email(auth_service, user_repository):
    user_repository.find_by_email.return_value = None

    with pytest.raises(InvalidCredentialsException):
        await auth_service.authenticate_user(
            LoginRequest(email="nobody@example.com", password="Secret@123")
        )


async def test_authenticate_user_raises_for_wrong_password(auth_service, user_repository):
    user_repository.find_by_email.return_value = _make_user()

    with pytest.raises(InvalidCredentialsException):
        await auth_service.authenticate_user(
            LoginRequest(email="atharv.gokhale@example.com", password="WrongPassword@1")
        )


async def test_authenticate_user_raises_for_inactive_account(auth_service, user_repository):
    inactive_user = _make_user(account_status=AccountStatus.INACTIVE)
    user_repository.find_by_email.return_value = inactive_user

    with pytest.raises(AccountInactiveException):
        await auth_service.authenticate_user(
            LoginRequest(email=inactive_user.email, password="Secret@123")
        )


# set_password


def _make_doctor_profile(**overrides) -> DoctorProfile:
    defaults = dict(
        id=PydanticObjectId(),
        user_id=PydanticObjectId(),
        qualification="MBBS",
        specialization=Specialization.CARDIOLOGY,
        experience_years=5,
        license_number="LIC-001",
        consultation_fee=500,
        clinic_address="Pune",
        approval_status=ApprovalStatus.APPROVED,
        setup_token_hash="hashed-token",
        setup_token_expiry=datetime.now(timezone.utc) + timedelta(hours=1),
    )
    defaults.update(overrides)
    return DoctorProfile(**defaults)


async def test_set_password_success(
    auth_service, user_repository, doctor_profile_repository, doctor_sync_service
):
    profile = _make_doctor_profile()
    doctor_user = _make_user(
        id=profile.user_id, role=Role.DOCTOR, account_status=AccountStatus.INACTIVE
    )
    doctor_profile_repository.find_by_setup_token_hash.return_value = profile
    user_repository.get_by_id.return_value = doctor_user

    result = await auth_service.set_password("raw-token", "NewSecret@1")

    assert result.account_status == AccountStatus.ACTIVE
    user_repository.save.assert_awaited_once_with(doctor_user)
    doctor_profile_repository.save.assert_awaited_once_with(profile)
    doctor_sync_service.sync_doctor.assert_awaited_once()
    assert profile.setup_token_hash is None
    assert profile.setup_token_expiry is None


async def test_set_password_raises_for_invalid_token(auth_service, doctor_profile_repository):
    doctor_profile_repository.find_by_setup_token_hash.return_value = None

    with pytest.raises(InvalidSetupTokenException):
        await auth_service.set_password("bad-token", "NewSecret@1")


async def test_set_password_raises_for_expired_token(auth_service, doctor_profile_repository):
    expired_profile = _make_doctor_profile(
        setup_token_expiry=datetime.now(timezone.utc) - timedelta(hours=1)
    )
    doctor_profile_repository.find_by_setup_token_hash.return_value = expired_profile

    with pytest.raises(SetupTokenExpiredException):
        await auth_service.set_password("raw-token", "NewSecret@1")


async def test_set_password_raises_for_missing_expiry(auth_service, doctor_profile_repository):
    profile_without_expiry = _make_doctor_profile(setup_token_expiry=None)
    doctor_profile_repository.find_by_setup_token_hash.return_value = profile_without_expiry

    with pytest.raises(SetupTokenExpiredException):
        await auth_service.set_password("raw-token", "NewSecret@1")


async def test_set_password_raises_when_user_no_longer_exists(
    auth_service, user_repository, doctor_profile_repository
):
    profile = _make_doctor_profile()
    doctor_profile_repository.find_by_setup_token_hash.return_value = profile
    user_repository.get_by_id.return_value = None

    with pytest.raises(UserNotFoundException):
        await auth_service.set_password("raw-token", "NewSecret@1")


# get_profile_by_id


async def test_get_profile_by_id_success(auth_service, user_repository):
    existing_user = _make_user()
    user_repository.get_by_id.return_value = existing_user

    result = await auth_service.get_profile_by_id(existing_user.id)

    assert result == existing_user


async def test_get_profile_by_id_raises_when_not_found(auth_service, user_repository):
    user_repository.get_by_id.return_value = None

    with pytest.raises(UserNotFoundException):
        await auth_service.get_profile_by_id(PydanticObjectId())
