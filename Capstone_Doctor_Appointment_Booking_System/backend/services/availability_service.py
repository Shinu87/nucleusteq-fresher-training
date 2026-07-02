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
from backend.schemas.request.slot_request import CreateSlotRequest, UpdateSlotRequest

logger = logging.getLogger(__name__)


async def _ensure_doctor_exists(doctor_id: PydanticObjectId) -> None:
    """
    Sanity check before allowing a doctor to create a slot.
    """
    doctor = await Doctor.get(doctor_id)
    if doctor is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Your doctor profile was not found in Appointment Service. "
            "Please contact an admin - the approval sync may have failed.",
        )


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
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="You already have a slot starting at this date and time",
        )

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
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Slot not found")

    if slot.doctor_id != doctor_id:
        # a doctor should never be able to touch another doctor's slot,
        # even if they somehow guess the slot's id
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="You can only manage your own availability slots",
        )

    return slot


async def update_slot(
    doctor_id: PydanticObjectId, slot_id: PydanticObjectId, payload: UpdateSlotRequest
) -> AvailabilitySlot:
    slot = await _get_owned_slot(doctor_id, slot_id)

    if slot.status != SlotStatus.AVAILABLE:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="A booked slot cannot be edited",
        )

    if payload.slot_date is not None:
        slot.slot_date = payload.slot_date
    if payload.start_time is not None:
        slot.start_time = payload.start_time
    if payload.end_time is not None:
        slot.end_time = payload.end_time

    if slot.end_time <= slot.start_time:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="end_time must be after start_time",
        )

    try:
        await slot.save()
    except DuplicateKeyError:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="You already have a slot starting at this date and time",
        )

    logger.info("Doctor %s updated slot %s", doctor_id, slot_id)
    return slot


async def delete_slot(doctor_id: PydanticObjectId, slot_id: PydanticObjectId) -> None:
    slot = await _get_owned_slot(doctor_id, slot_id)

    if slot.status != SlotStatus.AVAILABLE:
        # booked slots cannot be deleted
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="A booked slot cannot be deleted",
        )

    await slot.delete()
    logger.info("Doctor %s deleted slot %s", doctor_id, slot_id)