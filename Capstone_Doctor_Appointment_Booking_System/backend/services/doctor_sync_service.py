"""
Business logic for keeping the local Doctor read-model in sync.
"""

import logging

from beanie import PydanticObjectId
from fastapi import Depends

from backend.models.doctor import Doctor
from backend.repositories.doctor_repository import DoctorRepository, get_doctor_repository
from backend.schemas.request.internal_request import DoctorSyncRequest

logger = logging.getLogger(__name__)


class DoctorSyncService:

    def __init__(self, doctor_repository: DoctorRepository):
        self._doctor_repository = doctor_repository

    async def upsert_doctor(self, payload: DoctorSyncRequest) -> Doctor:
        doctor_id = PydanticObjectId(payload.doctor_id)
        existing_doctor = await self._doctor_repository.get_by_id(doctor_id)

        if existing_doctor is None:
            new_doctor = Doctor(
                id=doctor_id,
                full_name=payload.full_name,
                specialization=payload.specialization,
                qualification=payload.qualification,
                experience_years=payload.experience_years,
                consultation_fee=payload.consultation_fee,
                clinic_address=payload.clinic_address,
                is_active=payload.is_active,
            )
            await self._doctor_repository.insert(new_doctor)
            logger.info("Created doctor read-model for %s", payload.full_name)
            return new_doctor

        existing_doctor.full_name = payload.full_name
        existing_doctor.specialization = payload.specialization
        existing_doctor.qualification = payload.qualification
        existing_doctor.experience_years = payload.experience_years
        existing_doctor.consultation_fee = payload.consultation_fee
        existing_doctor.clinic_address = payload.clinic_address
        existing_doctor.is_active = payload.is_active
        await self._doctor_repository.save(existing_doctor)
        logger.info("Updated doctor read-model for %s", payload.full_name)
        return existing_doctor


def get_doctor_sync_service(
    doctor_repository: DoctorRepository = Depends(get_doctor_repository),
) -> DoctorSyncService:
    return DoctorSyncService(doctor_repository)