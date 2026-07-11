"""
Repository layer for the LeaveRequest document.
"""

from typing import Optional

from beanie import PydanticObjectId

from backend.constants.leave_request_status import LeaveRequestStatus
from backend.models.leave_request import LeaveRequest


class LeaveRequestRepository:

    async def get_by_id(self, leave_request_id: PydanticObjectId) -> Optional[LeaveRequest]:
        return await LeaveRequest.get(leave_request_id)

    async def insert(self, leave_request: LeaveRequest) -> LeaveRequest:
        await leave_request.insert()
        return leave_request

    async def save(self, leave_request: LeaveRequest) -> LeaveRequest:
        await leave_request.save()
        return leave_request

    async def find_by_doctor(self, doctor_id: PydanticObjectId) -> list[LeaveRequest]:
        return await LeaveRequest.find(
            LeaveRequest.doctor_id == doctor_id
        ).sort("-created_at").to_list()

    async def find_by_status(
        self, request_status: Optional[LeaveRequestStatus]
    ) -> list[LeaveRequest]:
        query_filter: dict = {}
        if request_status is not None:
            query_filter["request_status"] = request_status
        return await LeaveRequest.find(query_filter).sort("-created_at").to_list()


def get_leave_request_repository() -> LeaveRequestRepository:
    return LeaveRequestRepository()
