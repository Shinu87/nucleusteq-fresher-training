from unittest.mock import AsyncMock, MagicMock

import pytest_asyncio
from beanie import init_beanie

from backend.models.appointment import Appointment
from backend.models.availability_slot import AvailabilitySlot
from backend.models.doctor import Doctor
from backend.models.doctor_profile import DoctorProfile
from backend.models.notification import Notification
from backend.models.user import User


def _make_fake_database() -> MagicMock:
    fake_database = MagicMock()
    fake_database.command = AsyncMock(return_value={"version": "7.0.0"})
    fake_database.list_collection_names = AsyncMock(return_value=[])
    fake_database.client.append_metadata = None  

    fake_collection = MagicMock()
    fake_database.__getitem__ = MagicMock(return_value=fake_collection)
    fake_database.get_collection = MagicMock(return_value=fake_collection)
    return fake_database


@pytest_asyncio.fixture(autouse=True)
async def setup_test_database():
    await init_beanie(
        database=_make_fake_database(),
        document_models=[User, DoctorProfile, Doctor, AvailabilitySlot, Appointment, Notification],
        skip_indexes=True,
    )
    yield
