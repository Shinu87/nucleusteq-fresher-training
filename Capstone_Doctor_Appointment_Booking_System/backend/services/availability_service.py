"""
Business logic for a doctor managing their own availability slots.
"""

import logging

from beanie import PydanticObjectId
from fastapi import HTTPException, status
from pymongo.errors import DuplicateKeyError

from backend.constants.slot_status import SlotStatus
from backend.models.doctor import Doctor
from backend.models.availability_slot import AvailabilitySlot
from backend.schemas.request.slot_request import (
    BlockRangeRequest,
    CreateSlotRequest,
    GenerateSlotsRequest,
    UpdateSlotRequest,
)
from backend.schemas.response.slot_response import SlotTimeRange
from backend.exceptions.appointment_exception import (
    DuplicateSlotException,
    InvalidSlotTimeRangeException,
    NoSlotsGeneratedException,
    SlotNotDeletableException,
    SlotNotEditableException,
    SlotNotFoundException,
    SlotOwnershipException,
)
from backend.exceptions.doctor_exception import DoctorProfileSyncMissingException

logger = logging.getLogger(__name__)


async def _ensure_doctor_exists(doctor_id: PydanticObjectId) -> None:
    """
    Sanity check before allowing a doctor to create a slot.
    """
    doctor = await Doctor.get(doctor_id)
    if doctor is None:
        raise DoctorProfileSyncMissingException()

async def create_slot(doctor_id: PydanticObjectId, payload: CreateSlotRequest) -> AvailabilitySlot:
    await _ensure_doctor_exists(doctor_id)

    new_slot = AvailabilitySlot(
        doctor_id=doctor_id,
        slot_date=payload.slot_date,
        start_time=payload.start_time,
        end_time=payload.end_time,
        status=SlotStatus.AVAILABLE,
    )

    try:
        await new_slot.insert()
    except DuplicateKeyError:
        # the unique index on (doctor_id, slot_date, start_time) caught a duplicate
        raise DuplicateSlotException()

    logger.info("Doctor %s created slot %s %s-%s", doctor_id, payload.slot_date,
                payload.start_time, payload.end_time)
    return new_slot


async def list_my_slots(doctor_id: PydanticObjectId) -> list[AvailabilitySlot]:
    return await AvailabilitySlot.find(AvailabilitySlot.doctor_id == doctor_id).sort(
        "slot_date", "start_time"
    ).to_list()


async def _get_owned_slot(doctor_id: PydanticObjectId, slot_id: PydanticObjectId) -> AvailabilitySlot:
    slot = await AvailabilitySlot.get(slot_id)
    if slot is None:
        raise SlotNotFoundException()

    if slot.doctor_id != doctor_id:
        # a doctor should never be able to touch another doctor's slot,
        # even if they somehow guess the slot's id
        raise SlotOwnershipException()

    return slot


async def update_slot(
    doctor_id: PydanticObjectId, slot_id: PydanticObjectId, payload: UpdateSlotRequest
) -> AvailabilitySlot:
    slot = await _get_owned_slot(doctor_id, slot_id)

    if slot.status != SlotStatus.AVAILABLE:
        raise SlotNotEditableException()


    if payload.slot_date is not None:
        slot.slot_date = payload.slot_date
    if payload.start_time is not None:
        slot.start_time = payload.start_time
    if payload.end_time is not None:
        slot.end_time = payload.end_time

    if slot.end_time <= slot.start_time:
        raise InvalidSlotTimeRangeException()

    try:
        await slot.save()
    except DuplicateKeyError:
        raise DuplicateSlotException()

    logger.info("Doctor %s updated slot %s", doctor_id, slot_id)
    return slot


async def delete_slot(doctor_id: PydanticObjectId, slot_id: PydanticObjectId) -> None:
    slot = await _get_owned_slot(doctor_id, slot_id)

    if slot.status != SlotStatus.AVAILABLE:
        # booked slots cannot be deleted
        raise SlotNotDeletableException()

    await slot.delete()
    logger.info("Doctor %s deleted slot %s", doctor_id, slot_id)


# slot generation

def _time_to_minutes(value: str) -> int:
    hours, minutes = value.split(":")
    return int(hours) * 60 + int(minutes)


def _minutes_to_time(value: int) -> str:
    hours, minutes = divmod(value, 60)
    return f"{hours:02d}:{minutes:02d}"


def _build_slot_time_pairs(
    start_time: str, end_time: str, duration_minutes: int
) -> list[tuple[str, str]]:
    """
    Splits [start_time, end_time) into consecutive duration_minutes chunks.
    """
    start_minutes = _time_to_minutes(start_time)
    end_minutes = _time_to_minutes(end_time)

    pairs: list[tuple[str, str]] = []
    cursor = start_minutes
    while cursor + duration_minutes <= end_minutes:
        slot_start = _minutes_to_time(cursor)
        slot_end = _minutes_to_time(cursor + duration_minutes)
        pairs.append((slot_start, slot_end))
        cursor += duration_minutes

    return pairs


async def generate_slots(
    doctor_id: PydanticObjectId, payload: GenerateSlotsRequest
) -> tuple[list[AvailabilitySlot], list[SlotTimeRange], int]:
    """
    Expands a working window into individual AvailabilitySlot documents.
    """
    await _ensure_doctor_exists(doctor_id)

    time_pairs = _build_slot_time_pairs(
        payload.start_time, payload.end_time, payload.duration_minutes
    )
    if not time_pairs:
        raise NoSlotsGeneratedException()

    created: list[AvailabilitySlot] = []
    skipped: list[SlotTimeRange] = []

    for slot_start, slot_end in time_pairs:
        candidate = AvailabilitySlot(
            doctor_id=doctor_id,
            slot_date=payload.slot_date,
            start_time=slot_start,
            end_time=slot_end,
            status=SlotStatus.AVAILABLE,
        )
        try:
            await candidate.insert()
            created.append(candidate)
        except DuplicateKeyError:
            skipped.append(SlotTimeRange(start_time=slot_start, end_time=slot_end))

    logger.info(
        "Doctor %s generated slots for %s %s-%s (%s min): requested=%s created=%s skipped=%s",
        doctor_id, payload.slot_date, payload.start_time, payload.end_time,
        payload.duration_minutes, len(time_pairs), len(created), len(skipped),
    )
    return created, skipped, len(time_pairs)


# Block availability in a time range

async def block_range(
    doctor_id: PydanticObjectId, payload: BlockRangeRequest
) -> tuple[int, list[SlotTimeRange], list[SlotTimeRange]]:
    """
    Deletes every AVAILABLE slot owned by doctor_id on payload.slot_date
    """
    await _ensure_doctor_exists(doctor_id)

    slots_in_range = await AvailabilitySlot.find(
        AvailabilitySlot.doctor_id == doctor_id,
        AvailabilitySlot.slot_date == payload.slot_date,
        AvailabilitySlot.start_time >= payload.start_time,
        AvailabilitySlot.start_time < payload.end_time,
    ).to_list()

    blocked: list[SlotTimeRange] = []
    skipped: list[SlotTimeRange] = []

    for slot in slots_in_range:
        time_range = SlotTimeRange(start_time=slot.start_time, end_time=slot.end_time)
        if slot.status == SlotStatus.AVAILABLE:
            await slot.delete()
            blocked.append(time_range)
        else:
            skipped.append(time_range)

    logger.info(
        "Doctor %s blocked range %s %s-%s: found=%s blocked=%s skipped(booked)=%s",
        doctor_id, payload.slot_date, payload.start_time, payload.end_time,
        len(slots_in_range), len(blocked), len(skipped),
    )
    return len(slots_in_range), blocked, skipped