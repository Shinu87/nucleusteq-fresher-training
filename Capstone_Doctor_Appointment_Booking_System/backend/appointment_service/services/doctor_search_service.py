"""
Business logic for patients (or anyone logged in) searching for doctors
and viewing a single doctor's profile + available slots.
"""

import re
from datetime import date

from beanie import PydanticObjectId
from fastapi import HTTPException, status

from appointment_service.constants.slot_status import SlotStatus
from appointment_service.models.availability_slot import AvailabilitySlot
from appointment_service.models.doctor import Doctor


async def search_doctors(
    search: str | None,
    specialization: str | None,
    min_experience: int | None,
    max_fee: float | None,
) -> list[Doctor]:
    """
    Searches the local doctors read-model.

    Only active doctors are ever returned - a deactivated doctor should not show up in search results.
    """
    filters: dict = {"is_active": True}

    if specialization:
        filters["specialization"] = {"$regex": f"^{re.escape(specialization)}$", "$options": "i"}

    if search:
        filters["$text"] = {"$search": search}

    if min_experience is not None:
        filters["experience_years"] = {"$gte": min_experience}

    if max_fee is not None:
        filters["consultation_fee"] = {"$lte": max_fee}

    return await Doctor.find(filters).to_list()


async def get_doctor_detail(doctor_id: PydanticObjectId) -> tuple[Doctor, list[AvailabilitySlot]]:
    """
    Returns one doctor's profile plus their upcoming AVAILABLE slots.
    Slots that already happened, or that are already booked,
    are not shown here - a patient should only ever see slots they
    could actually book.
    """
    doctor = await Doctor.get(doctor_id)
    if doctor is None or not doctor.is_active:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Doctor not found")

    available_slots = await AvailabilitySlot.find(
        AvailabilitySlot.doctor_id == doctor_id,
        AvailabilitySlot.status == SlotStatus.AVAILABLE,
        AvailabilitySlot.slot_date >= date.today(),
    ).sort("slot_date", "start_time").to_list()

    return doctor, available_slots