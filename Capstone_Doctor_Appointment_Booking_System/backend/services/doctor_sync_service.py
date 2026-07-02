"""
Business logic for keeping the local Doctor read-model.
"""

import logging

from beanie import PydanticObjectId

from backend.models.doctor import Doctor
from backend.schemas.request.internal_request import DoctorSyncRequest

logger = logging.getLogger(__name__)


async def upsert_doctor(payload: DoctorSyncRequest) -> Doctor:
    """
    Creates the Doctor read-model row if it doesn't exist yet, or updates
    it if it does. "Upsert" because this same endpoint is reused both the
    first time a doctor is approved AND any time their profile changes
    later.
    """
    doctor_id = PydanticObjectId(payload.doctor_id)
    existing_doctor = await Doctor.get(doctor_id)

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
        await new_doctor.insert()
        logger.info("Created doctor read-model for %s", payload.full_name)
        return new_doctor

    existing_doctor.full_name = payload.full_name
    existing_doctor.specialization = payload.specialization
    existing_doctor.qualification = payload.qualification
    existing_doctor.experience_years = payload.experience_years
    existing_doctor.consultation_fee = payload.consultation_fee
    existing_doctor.clinic_address = payload.clinic_address
    existing_doctor.is_active = payload.is_active
    await existing_doctor.save()
    logger.info("Updated doctor read-model for %s", payload.full_name)
    return existing_doctor