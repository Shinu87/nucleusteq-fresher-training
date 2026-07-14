"""
Business logic for the Admin Module.
"""

import logging

from beanie import PydanticObjectId
from fastapi import Depends

from backend.constants.account_status import AccountStatus
from backend.constants.approval_status import ApprovalStatus
from backend.constants.roles import Role
from backend.exceptions.custom_exceptions import DoctorNotApprovedException
from backend.repositories.admin_repository import (
    AdminRepository,
    get_admin_repository,
)
from backend.services.doctor_profile_service import (
    DoctorProfileService,
    get_doctor_profile_service,
)
from backend.services.doctor_sync_service import (
    DoctorSyncService,
    get_doctor_sync_service,
)

logger = logging.getLogger(__name__)


class AdminService:

    def __init__(
        self,
        admin_repository: AdminRepository,
        doctor_profile_service: DoctorProfileService,
        doctor_sync_service: DoctorSyncService,
    ):
        self._admin_repository = admin_repository
        self._doctor_profile_service = doctor_profile_service
        self._doctor_sync_service = doctor_sync_service

    async def activate_doctor(
        self,
        doctor_profile_id: PydanticObjectId,
        admin_id: PydanticObjectId,
    ):
        profile, user = await self._doctor_profile_service._get_profile_and_user(
            doctor_profile_id
        )

        if profile.approval_status != ApprovalStatus.APPROVED:
            raise DoctorNotApprovedException()

        user.account_status = AccountStatus.ACTIVE
        await self._admin_repository.save_user(user)

        await self._doctor_sync_service.sync_doctor(
            user=user,
            profile=profile,
            is_active=True,
        )

        logger.info("Admin %s activated doctor account %s", admin_id, user.email)
        return user, profile

    async def deactivate_doctor(
        self,
        doctor_profile_id: PydanticObjectId,
        admin_id: PydanticObjectId,
    ):
        profile, user = await self._doctor_profile_service._get_profile_and_user(
            doctor_profile_id
        )

        if profile.approval_status != ApprovalStatus.APPROVED:
            raise DoctorNotApprovedException()

        user.account_status = AccountStatus.INACTIVE
        await self._admin_repository.save_user(user)

        await self._doctor_sync_service.sync_doctor(
            user=user,
            profile=profile,
            is_active=False,
        )

        logger.info("Admin %s deactivated doctor account %s", admin_id, user.email)
        return user, profile

    async def list_patients(
        self,
        account_status: AccountStatus | None = None,
    ):
        return await self._admin_repository.list_patients(account_status)

    async def get_platform_stats(self):
        return await self._admin_repository.get_platform_stats()


def get_admin_service(
    admin_repository: AdminRepository = Depends(get_admin_repository),
    doctor_profile_service: DoctorProfileService = Depends(
        get_doctor_profile_service
    ),
    doctor_sync_service: DoctorSyncService = Depends(
        get_doctor_sync_service
    ),
) -> AdminService:
    return AdminService(
        admin_repository,
        doctor_profile_service,
        doctor_sync_service,
    )