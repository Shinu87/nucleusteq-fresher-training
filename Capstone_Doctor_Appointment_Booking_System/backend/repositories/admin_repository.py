"""
Repository layer.
"""

from fastapi import Depends

from backend.constants.account_status import AccountStatus
from backend.constants.appointment_status import AppointmentStatus
from backend.constants.roles import Role
from backend.models.appointment import Appointment
from backend.models.doctor import Doctor
from backend.models.user import User
from backend.schemas.response.admin_response import AdminStatsResponse


class AdminRepository:

    async def save_user(self, user: User) -> None:
        await user.save()

    async def list_patients(
        self,
        account_status: AccountStatus | None = None,
    ) -> list[User]:
        query = {"role": Role.PATIENT}

        if account_status is not None:
            query["account_status"] = account_status

        return await User.find(query).sort("-created_at").to_list()

    async def get_platform_stats(self) -> AdminStatsResponse:
        total_doctors = await Doctor.find_all().count()
        active_doctors = await Doctor.find(
            Doctor.is_active == True  
        ).count()

        total_patients = await User.find(
            User.role == Role.PATIENT
        ).count()

        total_appointments = await Appointment.find_all().count()

        completed_appointments = await Appointment.find(
            Appointment.status == AppointmentStatus.COMPLETED
        ).count()

        cancelled_appointments = await Appointment.find(
            Appointment.status == AppointmentStatus.CANCELLED
        ).count()

        return AdminStatsResponse(
            total_doctors=total_doctors,
            active_doctors=active_doctors,
            total_patients=total_patients,
            total_appointments=total_appointments,
            completed_appointments=completed_appointments,
            cancelled_appointments=cancelled_appointments,
        )


def get_admin_repository() -> AdminRepository:
    return AdminRepository()