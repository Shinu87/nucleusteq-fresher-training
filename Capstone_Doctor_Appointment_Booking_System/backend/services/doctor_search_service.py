"""
Business logic for patients searching for doctors
and viewing a single doctor's profile + available slots.
"""

import re

from beanie import PydanticObjectId
from fastapi import Depends

from backend.models.availability_slot import AvailabilitySlot
from backend.models.doctor import Doctor
from backend.repositories.availability_repository import (
    AvailabilityRepository,
    get_availability_repository,
)
from backend.repositories.doctor_repository import DoctorRepository, get_doctor_repository
from backend.exceptions.custom_exceptions import DoctorNotFoundException
from backend.constants.specialization import Specialization

class DoctorSearchService:

    def __init__(
        self,
        doctor_repository: DoctorRepository,
        availability_repository: AvailabilityRepository,
    ):
        self._doctor_repository = doctor_repository
        self._availability_repository = availability_repository

    async def search_doctors(
        self,
        search: str | None,
        specialization: Specialization | None,
        min_experience: int | None,
        max_fee: float | None,
    ) -> list[Doctor]:

        filters: dict = {"is_active": True}

        if specialization:
            filters["specialization"] = {"$regex": f"^{re.escape(specialization)}$", "$options": "i"}

        if search:
            filters["$text"] = {"$search": search}

        if min_experience is not None:
            filters["experience_years"] = {"$gte": min_experience}

        if max_fee is not None:
            filters["consultation_fee"] = {"$lte": max_fee}

        return await self._doctor_repository.search(filters)

    async def get_doctor_detail(self, doctor_id: PydanticObjectId) -> tuple[Doctor, list[AvailabilitySlot]]:

        doctor = await self._doctor_repository.get_by_id(doctor_id)
        if doctor is None or not doctor.is_active:
            raise DoctorNotFoundException()

        available_slots = await self._availability_repository.find_available_upcoming(doctor_id)

        return doctor, available_slots


def get_doctor_search_service(
    doctor_repository: DoctorRepository = Depends(get_doctor_repository),
    availability_repository: AvailabilityRepository = Depends(get_availability_repository),
) -> DoctorSearchService:
    return DoctorSearchService(doctor_repository, availability_repository)
