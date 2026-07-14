from unittest.mock import AsyncMock

import pytest
from beanie import PydanticObjectId

from backend.constants.specialization import Specialization
from backend.models.doctor import Doctor
from backend.models.doctor_profile import DoctorProfile
from backend.models.user import User
from backend.constants.roles import Role
from backend.schemas.request.internal_request import DoctorSyncRequest
from backend.services.doctor_sync_service import DoctorSyncService


@pytest.fixture
def doctor_repository():
    return AsyncMock()


@pytest.fixture
def doctor_sync_service(doctor_repository):
    return DoctorSyncService(doctor_repository=doctor_repository)


def _make_user_and_profile():
    user_id = PydanticObjectId()
    user = User(
        id=user_id,
        full_name="Dr. Aniruddh Kulkarni",
        email="aniruddh.kulkarni@example.com",
        phone_number="9876543210",
        role=Role.DOCTOR,
    )
    profile = DoctorProfile(
        user_id=user_id,
        qualification="MBBS, MD",
        specialization=Specialization.CARDIOLOGY,
        experience_years=10,
        license_number="LIC-100",
        consultation_fee=800,
        clinic_address="Pune",
    )
    return user, profile


# sync_doctor -> delegates to upsert_doctor with a built payload


async def test_sync_doctor_builds_payload_and_delegates_to_upsert(doctor_sync_service, doctor_repository):
    user, profile = _make_user_and_profile()
    doctor_repository.get_by_id.return_value = None
    doctor_repository.insert.side_effect = lambda doctor: doctor

    await doctor_sync_service.sync_doctor(user=user, profile=profile, is_active=True)

    doctor_repository.insert.assert_awaited_once()
    inserted_doctor = doctor_repository.insert.await_args.args[0]
    assert inserted_doctor.full_name == user.full_name
    assert inserted_doctor.is_active is True


# upsert_doctor


def _make_payload(doctor_id: PydanticObjectId, **overrides) -> DoctorSyncRequest:
    defaults = dict(
        doctor_id=str(doctor_id),
        full_name="Dr. Aniruddh Kulkarni",
        specialization=Specialization.CARDIOLOGY,
        qualification="MBBS, MD",
        experience_years=10,
        consultation_fee=800,
        clinic_address="Pune",
        is_active=True,
    )
    defaults.update(overrides)
    return DoctorSyncRequest(**defaults)


async def test_upsert_doctor_creates_a_new_doctor_when_none_exists(doctor_sync_service, doctor_repository):
    doctor_id = PydanticObjectId()
    doctor_repository.get_by_id.return_value = None
    doctor_repository.insert.side_effect = lambda doctor: doctor

    result = await doctor_sync_service.upsert_doctor(_make_payload(doctor_id))

    assert isinstance(result, Doctor)
    assert result.id == doctor_id
    assert result.full_name == "Dr. Aniruddh Kulkarni"
    doctor_repository.insert.assert_awaited_once()
    doctor_repository.save.assert_not_awaited()


async def test_upsert_doctor_updates_the_existing_doctor(doctor_sync_service, doctor_repository):
    doctor_id = PydanticObjectId()
    existing_doctor = Doctor(
        id=doctor_id,
        full_name="Old Name",
        specialization=Specialization.DERMATOLOGY,
        qualification="Old Qualification",
        experience_years=1,
        consultation_fee=100,
        clinic_address="Old Address",
        is_active=False,
    )
    doctor_repository.get_by_id.return_value = existing_doctor
    doctor_repository.save.side_effect = lambda doctor: doctor

    result = await doctor_sync_service.upsert_doctor(
        _make_payload(doctor_id, full_name="New Name", is_active=True)
    )

    assert result.full_name == "New Name"
    assert result.is_active is True
    doctor_repository.save.assert_awaited_once_with(existing_doctor)
    doctor_repository.insert.assert_not_awaited()
