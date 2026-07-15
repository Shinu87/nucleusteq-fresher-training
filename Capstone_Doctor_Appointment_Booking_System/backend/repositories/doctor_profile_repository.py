"""
Repository layer for the DoctorProfile document.
"""

from typing import Optional

from beanie import PydanticObjectId

from backend.constants.approval_status import ApprovalStatus
from backend.models.doctor_profile import DoctorProfile


class DoctorProfileRepository:

    async def find_by_setup_token_hash(self, token_hash: str) -> Optional[DoctorProfile]:
        return await DoctorProfile.find_one(DoctorProfile.setup_token_hash == token_hash)

    async def find_by_license_number(self, license_number: str) -> Optional[DoctorProfile]:
        return await DoctorProfile.find_one(DoctorProfile.license_number == license_number)

    async def find_by_user_id(self, user_id: PydanticObjectId) -> Optional[DoctorProfile]:
        return await DoctorProfile.find_one(DoctorProfile.user_id == user_id)
    
    async def get_by_id(self, doctor_profile_id: PydanticObjectId) -> Optional[DoctorProfile]:
        return await DoctorProfile.get(doctor_profile_id)

    async def find_all(self) -> list[DoctorProfile]:
        return await DoctorProfile.find_all().to_list()

    async def find_by_approval_status(self, approval_status: ApprovalStatus) -> list[DoctorProfile]:
        return await DoctorProfile.find(
            DoctorProfile.approval_status == approval_status
        ).to_list()

    async def insert(self, profile: DoctorProfile) -> DoctorProfile:
        await profile.insert()
        return profile

    async def save(self, profile: DoctorProfile) -> DoctorProfile:
        await profile.save()
        return profile


def get_doctor_profile_repository() -> DoctorProfileRepository:
    return DoctorProfileRepository()