"""
Business logic for a doctor managing their own availability slots.
"""

import logging

from beanie import PydanticObjectId
from fastapi import Depends, HTTPException, status
from pymongo.errors import DuplicateKeyError

from backend.constants.slot_status import SlotStatus
from backend.models.availability_slot import AvailabilitySlot
from backend.repositories.availability_repository import (
    AvailabilityRepository,
    get_availability_repository,
)
from backend.repositories.doctor_repository import DoctorRepository, get_doctor_repository
from backend.schemas.request.slot_request import (
    BlockRangeRequest,
    CreateSlotRequest,
    GenerateSlotsRequest,
    UpdateSlotRequest,
)
from backend.schemas.response.slot_response import SlotTimeRange
from backend.exceptions.custom_exceptions import (
    DuplicateSlotException,
    InvalidSlotTimeRangeException,
    NoSlotsGeneratedException,
    SlotNotDeletableException,
    SlotNotEditableException,
    SlotNotFoundException,
    SlotOwnershipException,
    DoctorProfileSyncMissingException,
)

logger = logging.getLogger(__name__)


class AvailabilityService:

    def __init__(self, availability_repository: AvailabilityRepository, doctor_repository: DoctorRepository):
        self._availability_repository = availability_repository
        self._doctor_repository = doctor_repository

    async def _ensure_doctor_exists(self, doctor_id: PydanticObjectId) -> None:
        doctor = await self._doctor_repository.get_by_id(doctor_id)
        if doctor is None:
            raise DoctorProfileSyncMissingException()

    async def create_slot(self, doctor_id: PydanticObjectId, payload: CreateSlotRequest) -> AvailabilitySlot:
        await self._ensure_doctor_exists(doctor_id)

        new_slot = AvailabilitySlot(
            doctor_id=doctor_id,
            slot_date=payload.slot_date,
            start_time=payload.start_time,
            end_time=payload.end_time,
            status=SlotStatus.AVAILABLE,
        )

        try:
            await self._availability_repository.insert(new_slot)
        except DuplicateKeyError:
            raise DuplicateSlotException()

        logger.info("Doctor %s created slot %s %s-%s", doctor_id, payload.slot_date,
                    payload.start_time, payload.end_time)
        return new_slot

    async def list_my_slots(self, doctor_id: PydanticObjectId) -> list[AvailabilitySlot]:
        return await self._availability_repository.find_by_doctor(doctor_id)

    async def _get_owned_slot(self, doctor_id: PydanticObjectId, slot_id: PydanticObjectId) -> AvailabilitySlot:
        slot = await self._availability_repository.get_by_id(slot_id)
        if slot is None:
            raise SlotNotFoundException()

        if slot.doctor_id != doctor_id:
            raise SlotOwnershipException()

        return slot

    async def update_slot(
        self, doctor_id: PydanticObjectId, slot_id: PydanticObjectId, payload: UpdateSlotRequest
    ) -> AvailabilitySlot:
        slot = await self._get_owned_slot(doctor_id, slot_id)

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
            await self._availability_repository.save(slot)
        except DuplicateKeyError:
            raise DuplicateSlotException()

        logger.info("Doctor %s updated slot %s", doctor_id, slot_id)
        return slot

    async def delete_slot(self, doctor_id: PydanticObjectId, slot_id: PydanticObjectId) -> None:
        slot = await self._get_owned_slot(doctor_id, slot_id)

        if slot.status != SlotStatus.AVAILABLE:
            raise SlotNotDeletableException()

        await self._availability_repository.delete(slot)
        logger.info("Doctor %s deleted slot %s", doctor_id, slot_id)

    @staticmethod
    def _time_to_minutes(value: str) -> int:
        hours, minutes = value.split(":")
        return int(hours) * 60 + int(minutes)

    @staticmethod
    def _minutes_to_time(value: int) -> str:
        hours, minutes = divmod(value, 60)
        return f"{hours:02d}:{minutes:02d}"

    def _build_slot_time_pairs(
        self, start_time: str, end_time: str, duration_minutes: int
    ) -> list[tuple[str, str]]:
        start_minutes = self._time_to_minutes(start_time)
        end_minutes = self._time_to_minutes(end_time)

        pairs: list[tuple[str, str]] = []
        cursor = start_minutes
        while cursor + duration_minutes <= end_minutes:
            slot_start = self._minutes_to_time(cursor)
            slot_end = self._minutes_to_time(cursor + duration_minutes)
            pairs.append((slot_start, slot_end))
            cursor += duration_minutes

        return pairs

    async def generate_slots(
        self, doctor_id: PydanticObjectId, payload: GenerateSlotsRequest
    ) -> tuple[list[AvailabilitySlot], list[SlotTimeRange], int]:
        await self._ensure_doctor_exists(doctor_id)

        time_pairs = self._build_slot_time_pairs(
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
                await self._availability_repository.insert(candidate)
                created.append(candidate)
            except DuplicateKeyError:
                skipped.append(SlotTimeRange(start_time=slot_start, end_time=slot_end))

        logger.info(
            "Doctor %s generated slots for %s %s-%s (%s min): requested=%s created=%s skipped=%s",
            doctor_id, payload.slot_date, payload.start_time, payload.end_time,
            payload.duration_minutes, len(time_pairs), len(created), len(skipped),
        )
        return created, skipped, len(time_pairs)


    async def block_range(
        self, doctor_id: PydanticObjectId, payload: BlockRangeRequest
    ) -> tuple[int, list[SlotTimeRange], list[SlotTimeRange]]:
        await self._ensure_doctor_exists(doctor_id)

        slots_in_range = await self._availability_repository.find_in_time_range(
            doctor_id, payload.slot_date, payload.start_time, payload.end_time
        )

        blocked: list[SlotTimeRange] = []
        skipped: list[SlotTimeRange] = []

        for slot in slots_in_range:
            time_range = SlotTimeRange(start_time=slot.start_time, end_time=slot.end_time)
            if slot.status == SlotStatus.AVAILABLE:
                await self._availability_repository.delete(slot)
                blocked.append(time_range)
            else:
                skipped.append(time_range)

        logger.info(
            "Doctor %s blocked range %s %s-%s: found=%s blocked=%s skipped(booked)=%s",
            doctor_id, payload.slot_date, payload.start_time, payload.end_time,
            len(slots_in_range), len(blocked), len(skipped),
        )
        return len(slots_in_range), blocked, skipped


def get_availability_service(
    availability_repository: AvailabilityRepository = Depends(get_availability_repository),
    doctor_repository: DoctorRepository = Depends(get_doctor_repository),
) -> AvailabilityService:
    return AvailabilityService(availability_repository, doctor_repository)