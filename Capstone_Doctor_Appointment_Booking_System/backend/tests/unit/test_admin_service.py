from unittest.mock import AsyncMock

import pytest
from beanie import PydanticObjectId

from backend.constants.account_status import AccountStatus
from backend.constants.approval_status import ApprovalStatus
from backend.constants.roles import Role
from backend.constants.specialization import Specialization
from backend.exceptions.custom_exceptions import DoctorNotApprovedException
from backend.models.doctor_profile import DoctorProfile
from backend.models.user import User
from backend.services.admin_service import AdminService


@pytest.fixture
def admin_repository():
    return AsyncMock()


@pytest.fixture
def doctor_profile_service():
    return AsyncMock()


@pytest.fixture
def doctor_sync_service():
    return AsyncMock()


@pytest.fixture
def admin_service(admin_repository, doctor_profile_service, doctor_sync_service):
    return AdminService(
        admin_repository=admin_repository,
        doctor_profile_service=doctor_profile_service,
        doctor_sync_service=doctor_sync_service,
    )


def _make_user(**overrides) -> User:
    defaults = dict(
        id=PydanticObjectId(),
        full_name="Dr. Aniruddh Kulkarni",
        email="aniruddh.kulkarni@example.com",
        phone_number="9876543210",
        role=Role.DOCTOR,
        account_status=AccountStatus.INACTIVE,
    )
    defaults.update(overrides)
    return User(**defaults)


def _make_profile(**overrides) -> DoctorProfile:
    defaults = dict(
        id=PydanticObjectId(),
        user_id=PydanticObjectId(),
        qualification="MBBS, MD",
        specialization=Specialization.CARDIOLOGY,
        experience_years=10,
        license_number="LIC-100",
        consultation_fee=800,
        clinic_address="Pune",
        approval_status=ApprovalStatus.APPROVED,
    )
    defaults.update(overrides)
    return DoctorProfile(**defaults)


# activate_doctor

async def test_activate_doctor_success(admin_service, doctor_profile_service, admin_repository, doctor_sync_service):
    profile = _make_profile(approval_status=ApprovalStatus.APPROVED)
    user = _make_user(id=profile.user_id, account_status=AccountStatus.INACTIVE)
    doctor_profile_service._get_profile_and_user.return_value = (profile, user)

    result_user, result_profile = await admin_service.activate_doctor(
        doctor_profile_id=profile.id, admin_id=PydanticObjectId()
    )

    assert result_user.account_status == AccountStatus.ACTIVE
    assert result_profile == profile
    admin_repository.save_user.assert_awaited_once_with(user)
    doctor_sync_service.sync_doctor.assert_awaited_once_with(user=user, profile=profile, is_active=True)


async def test_activate_doctor_raises_when_profile_not_approved(admin_service, doctor_profile_service):
    profile = _make_profile(approval_status=ApprovalStatus.PENDING)
    user = _make_user(id=profile.user_id)
    doctor_profile_service._get_profile_and_user.return_value = (profile, user)

    with pytest.raises(DoctorNotApprovedException):
        await admin_service.activate_doctor(doctor_profile_id=profile.id, admin_id=PydanticObjectId())


# deactivate_doctor


async def test_deactivate_doctor_success(admin_service, doctor_profile_service, admin_repository, doctor_sync_service):
    profile = _make_profile(approval_status=ApprovalStatus.APPROVED)
    user = _make_user(id=profile.user_id, account_status=AccountStatus.ACTIVE)
    doctor_profile_service._get_profile_and_user.return_value = (profile, user)

    result_user, _ = await admin_service.deactivate_doctor(
        doctor_profile_id=profile.id, admin_id=PydanticObjectId()
    )

    assert result_user.account_status == AccountStatus.INACTIVE
    admin_repository.save_user.assert_awaited_once_with(user)
    doctor_sync_service.sync_doctor.assert_awaited_once_with(user=user, profile=profile, is_active=False)


async def test_deactivate_doctor_raises_when_profile_not_approved(admin_service, doctor_profile_service):
    profile = _make_profile(approval_status=ApprovalStatus.REJECTED)
    user = _make_user(id=profile.user_id)
    doctor_profile_service._get_profile_and_user.return_value = (profile, user)

    with pytest.raises(DoctorNotApprovedException):
        await admin_service.deactivate_doctor(doctor_profile_id=profile.id, admin_id=PydanticObjectId())


# list_patients


async def test_list_patients_delegates_to_repository_with_filter(admin_service, admin_repository):
    expected_patients = [_make_user(role=Role.PATIENT)]
    admin_repository.list_patients.return_value = expected_patients

    result = await admin_service.list_patients(account_status=AccountStatus.ACTIVE)

    assert result == expected_patients
    admin_repository.list_patients.assert_awaited_once_with(AccountStatus.ACTIVE)


async def test_list_patients_defaults_to_no_filter(admin_service, admin_repository):
    admin_repository.list_patients.return_value = []

    await admin_service.list_patients()

    admin_repository.list_patients.assert_awaited_once_with(None)


async def test_get_platform_stats_delegates_to_repository(admin_service, admin_repository):
    admin_repository.get_platform_stats.return_value = {"total_doctors": 5}

    result = await admin_service.get_platform_stats()

    assert result == {"total_doctors": 5}
    admin_repository.get_platform_stats.assert_awaited_once()
