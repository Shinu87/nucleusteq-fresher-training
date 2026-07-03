"""
Shared pytest fixtures for the backend test suite.

This file contains common test fixtures for database setup and cleanup,
ensuring each test runs with a clean and isolated database.
"""

import pytest_asyncio
from beanie import init_beanie
from pymongo import AsyncMongoClient

from backend.models.appointment import Appointment
from backend.models.availability_slot import AvailabilitySlot
from backend.models.doctor import Doctor
from backend.models.doctor_profile import DoctorProfile
from backend.models.notification import Notification
from backend.models.user import User

TEST_MONGO_URI = "mongodb://localhost:27017"
TEST_DB_NAME = "doctor_booking_test_db"


@pytest_asyncio.fixture(autouse=True)
async def setup_test_database():
    """
    Runs automatically before every single test function.
    """
    client = AsyncMongoClient(TEST_MONGO_URI)
    database = client[TEST_DB_NAME]

    await init_beanie(
        database=database,
        document_models=[User, DoctorProfile, Doctor, AvailabilitySlot, Appointment, Notification],
    )

    yield  # the actual test runs here

    await client.drop_database(TEST_DB_NAME)
    await client.close()
