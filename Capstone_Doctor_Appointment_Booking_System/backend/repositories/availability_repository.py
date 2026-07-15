"""
Repository layer for the AvailabilitySlot document.
"""

from datetime import date
from typing import Optional

from beanie import PydanticObjectId

from backend.constants.slot_status import SlotStatus
from backend.models.availability_slot import AvailabilitySlot


class AvailabilityRepository:

    async def get_by_id(self, slot_id: PydanticObjectId) -> Optional[AvailabilitySlot]:
        return await AvailabilitySlot.get(slot_id)

    async def insert(self, slot: AvailabilitySlot) -> AvailabilitySlot:
        await slot.insert()
        return slot

    async def save(self, slot: AvailabilitySlot) -> AvailabilitySlot:
        await slot.save()
        return slot

    async def delete(self, slot: AvailabilitySlot) -> None:
        await slot.delete()

    async def find_by_doctor(self, doctor_id: PydanticObjectId) -> list[AvailabilitySlot]:
        return await AvailabilitySlot.find(AvailabilitySlot.doctor_id == doctor_id).sort(
            "slot_date", "start_time"
        ).to_list()

    async def find_in_time_range(
        self,
        doctor_id: PydanticObjectId,
        slot_date: date,
        start_time: str,
        end_time: str,
    ) -> list[AvailabilitySlot]:
        return await AvailabilitySlot.find(
            AvailabilitySlot.doctor_id == doctor_id,
            AvailabilitySlot.slot_date == slot_date,
            AvailabilitySlot.start_time >= start_time,
            AvailabilitySlot.start_time < end_time,
        ).to_list()

    async def find_available_upcoming(self, doctor_id: PydanticObjectId) -> list[AvailabilitySlot]:
        return await AvailabilitySlot.find(
            AvailabilitySlot.doctor_id == doctor_id,
            AvailabilitySlot.status == SlotStatus.AVAILABLE,
            AvailabilitySlot.slot_date >= date.today(),
        ).sort("slot_date", "start_time").to_list()

    async def atomic_book_if_available(self, slot_id: PydanticObjectId) -> Optional[dict]:
        return await AvailabilitySlot.get_pymongo_collection().find_one_and_update(
            {"_id": slot_id, "status": SlotStatus.AVAILABLE},
            {"$set": {"status": SlotStatus.BOOKED}},
        )

    async def revert_to_available_after_duplicate(self, slot_id: PydanticObjectId) -> None:
        await AvailabilitySlot.get_motor_collection().update_one(
            {"_id": slot_id},
            {"$set": {"status": SlotStatus.AVAILABLE}},
        )

    async def release_slot(self, slot_id: PydanticObjectId) -> None:
        await AvailabilitySlot.get_pymongo_collection().update_one(
            {"_id": slot_id},
            {"$set": {"status": SlotStatus.AVAILABLE}},
        )


def get_availability_repository() -> AvailabilityRepository:
    return AvailabilityRepository()