"""
Tests for services/availability_service.py
"""

from datetime import date
from unittest.mock import AsyncMock

import pytest
from beanie import PydanticObjectId
from pymongo.errors import DuplicateKeyError

from backend.exceptions.custom_exceptions import NoSlotsGeneratedException
from backend.schemas.request.slot_request import (
    BlockRangeRequest,
    GenerateSlotsRequest,
)
from backend.schemas.response.slot_response import SlotTimeRange
from backend.constants.slot_status import SlotStatus
from backend.exceptions.custom_exceptions import (
    DoctorAccountInactiveException,
    DoctorProfileSyncMissingException,
    DuplicateSlotException,
    InvalidSlotTimeRangeException,
    SlotNotFoundException,
    SlotOwnershipException,
)
from backend.models.availability_slot import AvailabilitySlot
from backend.services.availability_service import AvailabilityService
from backend.schemas.request.slot_request import CreateSlotRequest
from backend.exceptions.custom_exceptions import (
    DuplicateSlotException,
    InvalidSlotTimeRangeException,
    SlotNotDeletableException,
    SlotNotEditableException,
)
from backend.schemas.request.slot_request import UpdateSlotRequest


@pytest.fixture
def availability_repository():
    return AsyncMock()


@pytest.fixture
def doctor_repository():
    return AsyncMock()


@pytest.fixture
def availability_service(
    availability_repository,
    doctor_repository,
):
    return AvailabilityService(
        availability_repository=availability_repository,
        doctor_repository=doctor_repository,
    )


@pytest.fixture
def doctor_id():
    return PydanticObjectId()


@pytest.fixture
def active_doctor():
    doctor = AsyncMock()
    doctor.is_active = True
    return doctor


@pytest.fixture
def inactive_doctor():
    doctor = AsyncMock()
    doctor.is_active = False
    return doctor


def make_slot(doctor_id):
    return AvailabilitySlot(
        id=PydanticObjectId(),
        doctor_id=doctor_id,
        slot_date=date(2026, 8, 1),
        start_time="09:00",
        end_time="09:30",
        status=SlotStatus.AVAILABLE,
    )


# create_slot()

async def test_create_slot_success(
    availability_service,
    availability_repository,
    doctor_repository,
    doctor_id,
    active_doctor,
):
    doctor_repository.get_by_id.return_value = active_doctor

    payload = CreateSlotRequest(
        slot_date=date(2026, 8, 1),
        start_time="09:00",
        end_time="09:30",
    )

    slot = await availability_service.create_slot(
        doctor_id,
        payload,
    )

    assert slot.doctor_id == doctor_id
    assert slot.status == SlotStatus.AVAILABLE

    availability_repository.insert.assert_awaited_once()


async def test_create_slot_invalid_time(
    availability_service,
    doctor_repository,
    doctor_id,
    active_doctor,
):
    doctor_repository.get_by_id.return_value = active_doctor

    payload = CreateSlotRequest(
        slot_date=date(2026, 8, 1),
        start_time="10:00",
        end_time="09:00",
    )

    with pytest.raises(InvalidSlotTimeRangeException):
        await availability_service.create_slot(
            doctor_id,
            payload,
        )


async def test_create_slot_duplicate(
    availability_service,
    availability_repository,
    doctor_repository,
    doctor_id,
    active_doctor,
):
    doctor_repository.get_by_id.return_value = active_doctor

    availability_repository.insert.side_effect = DuplicateKeyError("duplicate")

    payload = CreateSlotRequest(
        slot_date=date(2026, 8, 1),
        start_time="09:00",
        end_time="09:30",
    )

    with pytest.raises(DuplicateSlotException):
        await availability_service.create_slot(
            doctor_id,
            payload,
        )


async def test_create_slot_doctor_not_synced(
    availability_service,
    doctor_repository,
    doctor_id,
):
    doctor_repository.get_by_id.return_value = None

    payload = CreateSlotRequest(
        slot_date=date(2026, 8, 1),
        start_time="09:00",
        end_time="09:30",
    )

    with pytest.raises(DoctorProfileSyncMissingException):
        await availability_service.create_slot(
            doctor_id,
            payload,
        )


async def test_create_slot_inactive_doctor(
    availability_service,
    doctor_repository,
    doctor_id,
    inactive_doctor,
):
    doctor_repository.get_by_id.return_value = inactive_doctor

    payload = CreateSlotRequest(
        slot_date=date(2026, 8, 1),
        start_time="09:00",
        end_time="09:30",
    )

    with pytest.raises(DoctorAccountInactiveException):
        await availability_service.create_slot(
            doctor_id,
            payload,
        )


# list_my_slots()

async def test_list_my_slots(
    availability_service,
    availability_repository,
    doctor_id,
):
    slots = [
        make_slot(doctor_id),
        make_slot(doctor_id),
    ]

    availability_repository.find_by_doctor.return_value = slots

    result = await availability_service.list_my_slots(
        doctor_id,
    )

    assert result == slots

    availability_repository.find_by_doctor.assert_awaited_once_with(
        doctor_id,
    )


# _get_owned_slot()

async def test_get_owned_slot_success(
    availability_service,
    availability_repository,
    doctor_id,
):
    slot = make_slot(doctor_id)

    availability_repository.get_by_id.return_value = slot

    result = await availability_service._get_owned_slot(
        doctor_id,
        slot.id,
    )

    assert result == slot


async def test_get_owned_slot_not_found(
    availability_service,
    availability_repository,
    doctor_id,
):
    availability_repository.get_by_id.return_value = None

    with pytest.raises(SlotNotFoundException):
        await availability_service._get_owned_slot(
            doctor_id,
            PydanticObjectId(),
        )


async def test_get_owned_slot_wrong_owner(
    availability_service,
    availability_repository,
    doctor_id,
):
    slot = make_slot(PydanticObjectId())

    availability_repository.get_by_id.return_value = slot

    with pytest.raises(SlotOwnershipException):
        await availability_service._get_owned_slot(
            doctor_id,
            slot.id,
        )



# update_slot()

async def test_update_slot_success(
    availability_service,
    availability_repository,
    doctor_repository,
    doctor_id,
    active_doctor,
):
    doctor_repository.get_by_id.return_value = active_doctor

    slot = make_slot(doctor_id)

    availability_repository.get_by_id.return_value = slot

    payload = UpdateSlotRequest(
        slot_date=date(2026, 8, 2),
        start_time="10:00",
        end_time="10:30",
    )

    result = await availability_service.update_slot(
        doctor_id,
        slot.id,
        payload,
    )

    assert result.slot_date == date(2026, 8, 2)
    assert result.start_time == "10:00"
    assert result.end_time == "10:30"

    availability_repository.save.assert_awaited_once_with(slot)


async def test_update_slot_not_editable(
    availability_service,
    availability_repository,
    doctor_repository,
    doctor_id,
    active_doctor,
):
    doctor_repository.get_by_id.return_value = active_doctor

    slot = make_slot(doctor_id)
    slot.status = SlotStatus.BOOKED

    availability_repository.get_by_id.return_value = slot

    payload = UpdateSlotRequest(
        start_time="10:00",
    )

    with pytest.raises(SlotNotEditableException):
        await availability_service.update_slot(
            doctor_id,
            slot.id,
            payload,
        )


async def test_update_slot_invalid_time(
    availability_service,
    availability_repository,
    doctor_repository,
    doctor_id,
    active_doctor,
):
    doctor_repository.get_by_id.return_value = active_doctor

    slot = make_slot(doctor_id)

    availability_repository.get_by_id.return_value = slot

    payload = UpdateSlotRequest(
        start_time="11:00",
        end_time="10:00",
    )

    with pytest.raises(InvalidSlotTimeRangeException):
        await availability_service.update_slot(
            doctor_id,
            slot.id,
            payload,
        )


async def test_update_slot_duplicate(
    availability_service,
    availability_repository,
    doctor_repository,
    doctor_id,
    active_doctor,
):
    doctor_repository.get_by_id.return_value = active_doctor

    slot = make_slot(doctor_id)

    availability_repository.get_by_id.return_value = slot

    availability_repository.save.side_effect = DuplicateKeyError("duplicate")

    payload = UpdateSlotRequest(
        start_time="10:00",
        end_time="10:30",
    )

    with pytest.raises(DuplicateSlotException):
        await availability_service.update_slot(
            doctor_id,
            slot.id,
            payload,
        )


# delete_slot()

async def test_delete_slot_success(
    availability_service,
    availability_repository,
    doctor_repository,
    doctor_id,
    active_doctor,
):
    doctor_repository.get_by_id.return_value = active_doctor

    slot = make_slot(doctor_id)

    availability_repository.get_by_id.return_value = slot

    await availability_service.delete_slot(
        doctor_id,
        slot.id,
    )

    availability_repository.delete.assert_awaited_once_with(slot)


async def test_delete_slot_not_deletable(
    availability_service,
    availability_repository,
    doctor_repository,
    doctor_id,
    active_doctor,
):
    doctor_repository.get_by_id.return_value = active_doctor

    slot = make_slot(doctor_id)
    slot.status = SlotStatus.BOOKED

    availability_repository.get_by_id.return_value = slot

    with pytest.raises(SlotNotDeletableException):
        await availability_service.delete_slot(
            doctor_id,
            slot.id,
        )


# Helper methods

def test_time_to_minutes():
    assert AvailabilityService._time_to_minutes("00:00") == 0
    assert AvailabilityService._time_to_minutes("01:30") == 90
    assert AvailabilityService._time_to_minutes("09:15") == 555


def test_minutes_to_time():
    assert AvailabilityService._minutes_to_time(0) == "00:00"
    assert AvailabilityService._minutes_to_time(90) == "01:30"
    assert AvailabilityService._minutes_to_time(555) == "09:15"


def test_build_slot_time_pairs(availability_service):
    pairs = availability_service._build_slot_time_pairs(
        "09:00",
        "10:30",
        30,
    )

    assert pairs == [
        ("09:00", "09:30"),
        ("09:30", "10:00"),
        ("10:00", "10:30"),
    ]


def test_build_slot_time_pairs_empty(availability_service):
    pairs = availability_service._build_slot_time_pairs(
        "09:00",
        "09:10",
        30,
    )

    assert pairs == []


# generate_slots()

async def test_generate_slots_success(
    availability_service,
    availability_repository,
    doctor_repository,
    doctor_id,
    active_doctor,
):
    doctor_repository.get_by_id.return_value = active_doctor

    payload = GenerateSlotsRequest(
        slot_date=date(2026, 8, 1),
        start_time="09:00",
        end_time="10:30",
        duration_minutes=30,
    )

    created, skipped, requested = await availability_service.generate_slots(
        doctor_id,
        payload,
    )

    assert requested == 3
    assert len(created) == 3
    assert skipped == []

    assert availability_repository.insert.await_count == 3


async def test_generate_slots_with_duplicates(
    availability_service,
    availability_repository,
    doctor_repository,
    doctor_id,
    active_doctor,
):
    doctor_repository.get_by_id.return_value = active_doctor

    counter = {"value": 0}

    async def insert(slot):
        counter["value"] += 1
        if counter["value"] == 2:
            raise DuplicateKeyError("duplicate")

    availability_repository.insert.side_effect = insert

    payload = GenerateSlotsRequest(
        slot_date=date(2026, 8, 1),
        start_time="09:00",
        end_time="10:30",
        duration_minutes=30,
    )

    created, skipped, requested = await availability_service.generate_slots(
        doctor_id,
        payload,
    )

    assert requested == 3
    assert len(created) == 2
    assert len(skipped) == 1

    assert skipped[0].start_time == "09:30"
    assert skipped[0].end_time == "10:00"


async def test_generate_slots_none_generated(
    availability_service,
    doctor_repository,
    doctor_id,
    active_doctor,
):
    doctor_repository.get_by_id.return_value = active_doctor

    payload = GenerateSlotsRequest(
        slot_date=date(2026, 8, 1),
        start_time="09:00",
        end_time="09:10",
        duration_minutes=30,
    )

    with pytest.raises(NoSlotsGeneratedException):
        await availability_service.generate_slots(
            doctor_id,
            payload,
        )


# block_range()

async def test_block_range_success(
    availability_service,
    availability_repository,
    doctor_repository,
    doctor_id,
    active_doctor,
):
    doctor_repository.get_by_id.return_value = active_doctor

    slot1 = make_slot(doctor_id)

    slot2 = AvailabilitySlot(
        doctor_id=doctor_id,
        slot_date=date(2026, 8, 1),
        start_time="09:30",
        end_time="10:00",
        status=SlotStatus.AVAILABLE,
    )

    availability_repository.find_in_time_range.return_value = [
        slot1,
        slot2,
    ]

    payload = BlockRangeRequest(
        slot_date=date(2026, 8, 1),
        start_time="09:00",
        end_time="10:00",
    )

    total, blocked, skipped = await availability_service.block_range(
        doctor_id,
        payload,
    )

    assert total == 2
    assert len(blocked) == 2
    assert skipped == []

    assert availability_repository.delete.await_count == 2


async def test_block_range_skips_booked_slots(
    availability_service,
    availability_repository,
    doctor_repository,
    doctor_id,
    active_doctor,
):
    doctor_repository.get_by_id.return_value = active_doctor

    available_slot = make_slot(doctor_id)

    booked_slot = AvailabilitySlot(
        doctor_id=doctor_id,
        slot_date=date(2026, 8, 1),
        start_time="09:30",
        end_time="10:00",
        status=SlotStatus.BOOKED,
    )

    availability_repository.find_in_time_range.return_value = [
        available_slot,
        booked_slot,
    ]

    payload = BlockRangeRequest(
        slot_date=date(2026, 8, 1),
        start_time="09:00",
        end_time="10:00",
    )

    total, blocked, skipped = await availability_service.block_range(
        doctor_id,
        payload,
    )

    assert total == 2
    assert len(blocked) == 1
    assert len(skipped) == 1

    availability_repository.delete.assert_awaited_once_with(
        available_slot,
    )


async def test_block_range_no_slots(
    availability_service,
    availability_repository,
    doctor_repository,
    doctor_id,
    active_doctor,
):
    doctor_repository.get_by_id.return_value = active_doctor

    availability_repository.find_in_time_range.return_value = []

    payload = BlockRangeRequest(
        slot_date=date(2026, 8, 1),
        start_time="09:00",
        end_time="10:00",
    )

    total, blocked, skipped = await availability_service.block_range(
        doctor_id,
        payload,
    )

    assert total == 0
    assert blocked == []
    assert skipped == []

    availability_repository.delete.assert_not_awaited()