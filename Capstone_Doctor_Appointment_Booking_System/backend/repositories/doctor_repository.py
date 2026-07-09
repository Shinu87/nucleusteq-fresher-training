"""
Repository layer for the Doctor (read-model) document.
"""

from typing import Optional

from beanie import PydanticObjectId

from backend.models.doctor import Doctor


class DoctorRepository:

    async def get_by_id(self, doctor_id: PydanticObjectId) -> Optional[Doctor]:
        return await Doctor.get(doctor_id)

    async def insert(self, doctor: Doctor) -> Doctor:
        await doctor.insert()
        return doctor

    async def save(self, doctor: Doctor) -> Doctor:
        await doctor.save()
        return doctor

    async def search(self, filters: dict) -> list[Doctor]:
        return await Doctor.find(filters).to_list()


def get_doctor_repository() -> DoctorRepository:
    return DoctorRepository()