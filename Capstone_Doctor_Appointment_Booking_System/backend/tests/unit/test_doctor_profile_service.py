from unittest.mock import AsyncMock

import pytest
from beanie import PydanticObjectId

from backend.constants.account_status import AccountStatus
from backend.constants.approval_status import ApprovalStatus
from backend.constants.gender import Gender
from backend.constants.roles import Role
from backend.constants.specialization import Specialization
from backend.exceptions.custom_exceptions import (
    ApplicationAlreadyReviewedException,
    DoctorApplicationNotFoundException,
    DoctorNotApprovedException,
    DuplicateLicenseException,
    EmailAlreadyRegisteredException,
    UserNotFoundException,
)
from backend.models.doctor_profile import DoctorProfile
from backend.models.user import User
from backend.schemas.request.doctor_request import DoctorRegisterRequest
from backend.services.doctor_profile_service import DoctorProfileService


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
def notification_service():
    return AsyncMock()


@pytest.fixture
def doctor_profile_service(
    user_repository, doctor_profile_repository, doctor_sync_service, notification_service
):
    return DoctorProfileService(
        user_repository=user_repository,
        doctor_profile_repository=doctor_profile_repository,
        doctor_sync_service=doctor_sync_service,
        notification_service=notification_service,
    )


def _make_application_payload() -> DoctorRegisterRequest:
    return DoctorRegisterRequest(
        full_name="Aniruddh Kulkarni",
        email="aniruddh.kulkarni@example.com",
        phone_number="9876543210",
        qualification="MBBS, MD",
        specialization=Specialization.CARDIOLOGY,
        experience_years=10,
        license_number="LIC-100",
        consultation_fee=800,
        clinic_address="Baner Road, Pune",
        gender=Gender.MALE,
    )


def _make_user(**overrides) -> User:
    defaults = dict(
        id=PydanticObjectId(),
        full_name="Aniruddh Kulkarni",
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
        approval_status=ApprovalStatus.PENDING,
    )
    defaults.update(overrides)
    return DoctorProfile(**defaults)


# submit_doctor_application


async def test_submit_doctor_application_success(
    doctor_profile_service, user_repository, doctor_profile_repository
):
    def _fake_insert_assigns_id(user):
        user.id = PydanticObjectId()
        return user

    user_repository.find_by_email.return_value = None
    doctor_profile_repository.find_by_license_number.return_value = None
    user_repository.insert.side_effect = _fake_insert_assigns_id
    doctor_profile_repository.insert.side_effect = lambda profile: profile

    user, profile = await doctor_profile_service.submit_doctor_application(_make_application_payload())

    assert user.role == Role.DOCTOR
    assert user.account_status == AccountStatus.INACTIVE
    assert profile.approval_status == ApprovalStatus.PENDING
    assert profile.user_id == user.id


async def test_submit_doctor_application_raises_when_email_already_registered(
    doctor_profile_service, user_repository
):
    user_repository.find_by_email.return_value = _make_user()

    with pytest.raises(EmailAlreadyRegisteredException):
        await doctor_profile_service.submit_doctor_application(_make_application_payload())


async def test_submit_doctor_application_raises_when_license_already_registered(
    doctor_profile_service, user_repository, doctor_profile_repository
):
    user_repository.find_by_email.return_value = None
    doctor_profile_repository.find_by_license_number.return_value = _make_profile()

    with pytest.raises(DuplicateLicenseException):
        await doctor_profile_service.submit_doctor_application(_make_application_payload())


# list_doctor_applications


async def test_list_doctor_applications_filters_by_status(
    doctor_profile_service, doctor_profile_repository, user_repository
):
    profile = _make_profile()
    user = _make_user(id=profile.user_id)
    doctor_profile_repository.find_by_approval_status.return_value = [profile]
    user_repository.get_by_id.return_value = user

    results = await doctor_profile_service.list_doctor_applications(ApprovalStatus.PENDING)

    assert results == [{"user": user, "profile": profile}]
    doctor_profile_repository.find_by_approval_status.assert_awaited_once_with(ApprovalStatus.PENDING)
    doctor_profile_repository.find_all.assert_not_awaited()


async def test_list_doctor_applications_returns_all_when_no_filter(
    doctor_profile_service, doctor_profile_repository, user_repository
):
    profile = _make_profile()
    user_repository.get_by_id.return_value = _make_user(id=profile.user_id)
    doctor_profile_repository.find_all.return_value = [profile]

    results = await doctor_profile_service.list_doctor_applications()

    assert len(results) == 1
    doctor_profile_repository.find_all.assert_awaited_once()


async def test_list_doctor_applications_skips_profiles_with_missing_user(
    doctor_profile_service, doctor_profile_repository, user_repository
):
    profile = _make_profile()
    doctor_profile_repository.find_all.return_value = [profile]
    user_repository.get_by_id.return_value = None

    results = await doctor_profile_service.list_doctor_applications()

    assert results == []


# approve_doctor


async def test_approve_doctor_success(
    doctor_profile_service,
    doctor_profile_repository,
    user_repository,
    notification_service,
    doctor_sync_service,
):
    profile = _make_profile(approval_status=ApprovalStatus.PENDING)
    user = _make_user(id=profile.user_id)
    doctor_profile_repository.get_by_id.return_value = profile
    user_repository.get_by_id.return_value = user

    result_user, result_profile = await doctor_profile_service.approve_doctor(
        doctor_profile_id=profile.id, admin_id=PydanticObjectId()
    )

    assert result_profile.approval_status == ApprovalStatus.APPROVED
    assert result_profile.setup_token_hash is not None
    assert result_profile.setup_token_expiry is not None
    doctor_profile_repository.save.assert_awaited_once_with(profile)
    notification_service.send_notification.assert_awaited_once()
    doctor_sync_service.upsert_doctor.assert_awaited_once()
    assert result_user == user


async def test_approve_doctor_raises_when_profile_not_found(
    doctor_profile_service, doctor_profile_repository
):
    doctor_profile_repository.get_by_id.return_value = None

    with pytest.raises(DoctorApplicationNotFoundException):
        await doctor_profile_service.approve_doctor(
            doctor_profile_id=PydanticObjectId(), admin_id=PydanticObjectId()
        )


async def test_approve_doctor_raises_when_linked_user_missing(
    doctor_profile_service, doctor_profile_repository, user_repository
):
    profile = _make_profile()
    doctor_profile_repository.get_by_id.return_value = profile
    user_repository.get_by_id.return_value = None

    with pytest.raises(UserNotFoundException):
        await doctor_profile_service.approve_doctor(
            doctor_profile_id=profile.id, admin_id=PydanticObjectId()
        )


async def test_approve_doctor_raises_when_already_reviewed(
    doctor_profile_service, doctor_profile_repository, user_repository
):
    profile = _make_profile(approval_status=ApprovalStatus.APPROVED)
    user = _make_user(id=profile.user_id)
    doctor_profile_repository.get_by_id.return_value = profile
    user_repository.get_by_id.return_value = user

    with pytest.raises(ApplicationAlreadyReviewedException):
        await doctor_profile_service.approve_doctor(
            doctor_profile_id=profile.id, admin_id=PydanticObjectId()
        )


# reject_doctor


async def test_reject_doctor_success_deactivates_user(
    doctor_profile_service, doctor_profile_repository, user_repository
):
    profile = _make_profile(approval_status=ApprovalStatus.PENDING)
    user = _make_user(id=profile.user_id, account_status=AccountStatus.ACTIVE)
    doctor_profile_repository.get_by_id.return_value = profile
    user_repository.get_by_id.return_value = user

    result_user, result_profile = await doctor_profile_service.reject_doctor(
        doctor_profile_id=profile.id, admin_id=PydanticObjectId(), reason="Incomplete documents"
    )

    assert result_profile.approval_status == ApprovalStatus.REJECTED
    assert result_user.account_status == AccountStatus.INACTIVE
    user_repository.save.assert_awaited_once_with(user)


async def test_reject_doctor_does_not_resave_user_if_already_inactive(
    doctor_profile_service, doctor_profile_repository, user_repository
):
    profile = _make_profile(approval_status=ApprovalStatus.PENDING)
    user = _make_user(id=profile.user_id, account_status=AccountStatus.INACTIVE)
    doctor_profile_repository.get_by_id.return_value = profile
    user_repository.get_by_id.return_value = user

    await doctor_profile_service.reject_doctor(
        doctor_profile_id=profile.id, admin_id=PydanticObjectId(), reason=None
    )

    user_repository.save.assert_not_awaited()


async def test_reject_doctor_raises_when_already_reviewed(
    doctor_profile_service, doctor_profile_repository, user_repository
):
    profile = _make_profile(approval_status=ApprovalStatus.REJECTED)
    user = _make_user(id=profile.user_id)
    doctor_profile_repository.get_by_id.return_value = profile
    user_repository.get_by_id.return_value = user

    with pytest.raises(ApplicationAlreadyReviewedException):
        await doctor_profile_service.reject_doctor(
            doctor_profile_id=profile.id, admin_id=PydanticObjectId(), reason="n/a"
        )


# set_own_account_status


async def test_set_own_account_status_success(
    doctor_profile_service, doctor_profile_repository, user_repository, doctor_sync_service
):
    profile = _make_profile(approval_status=ApprovalStatus.APPROVED)
    user = _make_user(id=profile.user_id, account_status=AccountStatus.ACTIVE)
    doctor_profile_repository.find_by_user_id.return_value = profile
    user_repository.get_by_id.return_value = user

    result_user, result_profile = await doctor_profile_service.set_own_account_status(
        doctor_user_id=user.id, new_status=AccountStatus.INACTIVE
    )

    assert result_user.account_status == AccountStatus.INACTIVE
    doctor_sync_service.sync_doctor.assert_awaited_once_with(user=user, profile=profile, is_active=False)


async def test_set_own_account_status_raises_when_profile_missing(
    doctor_profile_service, doctor_profile_repository
):
    doctor_profile_repository.find_by_user_id.return_value = None

    with pytest.raises(DoctorApplicationNotFoundException):
        await doctor_profile_service.set_own_account_status(
            doctor_user_id=PydanticObjectId(), new_status=AccountStatus.ACTIVE
        )


async def test_set_own_account_status_raises_when_not_approved(
    doctor_profile_service, doctor_profile_repository
):
    doctor_profile_repository.find_by_user_id.return_value = _make_profile(
        approval_status=ApprovalStatus.PENDING
    )

    with pytest.raises(DoctorNotApprovedException):
        await doctor_profile_service.set_own_account_status(
            doctor_user_id=PydanticObjectId(), new_status=AccountStatus.ACTIVE
        )


async def test_set_own_account_status_raises_when_user_missing(
    doctor_profile_service, doctor_profile_repository, user_repository
):
    profile = _make_profile(approval_status=ApprovalStatus.APPROVED)
    doctor_profile_repository.find_by_user_id.return_value = profile
    user_repository.get_by_id.return_value = None

    with pytest.raises(UserNotFoundException):
        await doctor_profile_service.set_own_account_status(
            doctor_user_id=profile.user_id, new_status=AccountStatus.ACTIVE
        )
