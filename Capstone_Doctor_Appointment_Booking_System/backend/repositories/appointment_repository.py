"""
Repository layer for the Appointment document.
"""

from typing import Optional

from beanie import PydanticObjectId

from backend.constants.appointment_status import AppointmentStatus
from backend.models.appointment import Appointment


class AppointmentRepository:

    async def get_by_id(self, appointment_id: PydanticObjectId) -> Optional[Appointment]:
        return await Appointment.get(appointment_id)

    async def insert(self, appointment: Appointment) -> Appointment:
        await appointment.insert()
        return appointment

    async def save(self, appointment: Appointment) -> Appointment:
        await appointment.save()
        return appointment

    async def find_by_patient(
        self,
        patient_id: PydanticObjectId,
        status_filter: Optional[AppointmentStatus],
    ) -> list[Appointment]:
        query_filter: dict = {"patient_id": patient_id}
        if status_filter is not None:
            query_filter["status"] = status_filter

        return await Appointment.find(query_filter).sort("-booked_at").to_list()

    async def find_by_doctor(
        self,
        doctor_id: PydanticObjectId,
        status_filter: Optional[AppointmentStatus],
        sort_order: str = "asc",
    ) -> list[Appointment]:
        query_filter: dict = {"doctor_id": doctor_id}
        if status_filter is not None:
            query_filter["status"] = status_filter

        direction = 1 if sort_order == "asc" else -1
        return (
            await Appointment.find(query_filter)
            .sort([("appointment_date", direction), ("start_time", direction)])
            .to_list()
        )


def get_appointment_repository() -> AppointmentRepository:
    return AppointmentRepository()