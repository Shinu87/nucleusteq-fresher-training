"""
Business logic for a doctor's emergency leave / cancellation request:

    Doctor submits request (PENDING) -> Admin Approve/Reject
        -> On approval: cancel affected appointments, free/remove
           affected slots, email every affected patient (reusing the
           existing SMTP notification service).
"""

import logging
from datetime import datetime, timezone

from beanie import PydanticObjectId
from fastapi import Depends

from backend.constants.appointment_status import AppointmentStatus
from backend.constants.leave_request_status import LeaveRequestStatus
from backend.constants.slot_status import SlotStatus
from backend.models.leave_request import LeaveRequest
from backend.models.notification import NotificationType
from backend.repositories.appointment_repository import (
    AppointmentRepository,
    get_appointment_repository,
)
from backend.repositories.availability_repository import (
    AvailabilityRepository,
    get_availability_repository,
)
from backend.repositories.doctor_repository import DoctorRepository, get_doctor_repository
from backend.repositories.leave_request_repository import (
    LeaveRequestRepository,
    get_leave_request_repository,
)
from backend.repositories.user_repository import UserRepository, get_user_repository
from backend.schemas.request.internal_request import SendNotificationRequest
from backend.schemas.request.leave_request_request import LeaveRequestCreate
from backend.schemas.response.leave_request_response import (
    LeaveRequestResponse,
    to_leave_request_response,
)
from backend.services.notification_service import NotificationService, get_notification_service
from backend.exceptions.custom_exceptions import (
    DoctorProfileSyncMissingException,
    InvalidLeaveTimeRangeException,
    LeaveRequestAlreadyReviewedException,
    LeaveRequestNotFoundException,
)

logger = logging.getLogger(__name__)


def _utc_now() -> datetime:
    return datetime.now(timezone.utc)


class LeaveRequestService:

    def __init__(
        self,
        leave_request_repository: LeaveRequestRepository,
        availability_repository: AvailabilityRepository,
        appointment_repository: AppointmentRepository,
        doctor_repository: DoctorRepository,
        user_repository: UserRepository,
        notification_service: NotificationService,
    ):
        self._leave_request_repository = leave_request_repository
        self._availability_repository = availability_repository
        self._appointment_repository = appointment_repository
        self._doctor_repository = doctor_repository
        self._user_repository = user_repository
        self._notification_service = notification_service

    async def _ensure_doctor_exists(self, doctor_id: PydanticObjectId) -> None:
        doctor = await self._doctor_repository.get_by_id(doctor_id)
        if doctor is None:
            raise DoctorProfileSyncMissingException()

    async def request_leave(
        self, doctor_id: PydanticObjectId, payload: LeaveRequestCreate
    ) -> LeaveRequest:
        await self._ensure_doctor_exists(doctor_id)

        if payload.end_time <= payload.start_time:
            raise InvalidLeaveTimeRangeException()

        leave_request = LeaveRequest(
            doctor_id=doctor_id,
            date=payload.date,
            start_time=payload.start_time,
            end_time=payload.end_time,
            reason=payload.reason,
            request_status=LeaveRequestStatus.PENDING,
        )
        await self._leave_request_repository.insert(leave_request)

        logger.info(
            "Doctor %s requested emergency leave %s %s-%s",
            doctor_id, payload.date, payload.start_time, payload.end_time,
        )
        return leave_request

    async def list_my_requests(self, doctor_id: PydanticObjectId) -> list[LeaveRequest]:
        return await self._leave_request_repository.find_by_doctor(doctor_id)

    async def list_requests(
        self, request_status: LeaveRequestStatus | None
    ) -> list[LeaveRequest]:
        return await self._leave_request_repository.find_by_status(request_status)

    async def _get_pending_request(self, leave_request_id: PydanticObjectId) -> LeaveRequest:
        leave_request = await self._leave_request_repository.get_by_id(leave_request_id)
        if leave_request is None:
            raise LeaveRequestNotFoundException()

        if leave_request.request_status != LeaveRequestStatus.PENDING:
            raise LeaveRequestAlreadyReviewedException(
                leave_request.request_status.value.lower()
            )

        return leave_request

    async def reject_leave(
        self,
        leave_request_id: PydanticObjectId,
        admin_id: PydanticObjectId,
        rejection_reason: str,
    ) -> LeaveRequest:
        leave_request = await self._get_pending_request(leave_request_id)

        leave_request.request_status = LeaveRequestStatus.REJECTED
        leave_request.rejection_reason = rejection_reason
        leave_request.approved_by = admin_id
        leave_request.approved_at = _utc_now()
        leave_request.updated_at = _utc_now()
        await self._leave_request_repository.save(leave_request)

        logger.info(
            "Admin %s rejected leave request %s (reason: %s)",
            admin_id, leave_request_id, rejection_reason,
        )
        return leave_request

    async def approve_leave(
        self, leave_request_id: PydanticObjectId, admin_id: PydanticObjectId
    ) -> tuple[LeaveRequest, int]:
        leave_request = await self._get_pending_request(leave_request_id)

        slots_in_range = await self._availability_repository.find_in_time_range(
            leave_request.doctor_id,
            leave_request.date,
            leave_request.start_time,
            leave_request.end_time,
        )

        cancelled_count = 0
        for slot in slots_in_range:
            if slot.status == SlotStatus.BOOKED:
                appointment = await self._appointment_repository.find_by_slot_id(slot.id)
                if appointment is not None and appointment.status == AppointmentStatus.BOOKED:
                    appointment.status = AppointmentStatus.CANCELLED
                    appointment.cancelled_at = _utc_now()
                    await self._appointment_repository.save(appointment)
                    cancelled_count += 1

                    await self._notification_service.send_notification(SendNotificationRequest(
                        recipient_email=appointment.patient_email,
                        type=NotificationType.APPOINTMENT_CANCELLATION,
                        payload={
                            "patient_name": appointment.patient_name,
                            "doctor_name": appointment.doctor_name,
                            "appointment_date": str(appointment.appointment_date),
                            "start_time": appointment.start_time,
                            "reason": leave_request.reason,
                        },
                    ))

            # The doctor is unavailable for this whole range, so the slot
            # (whether it was AVAILABLE or just-cancelled BOOKED) should
            # not remain bookable - remove it entirely, same as block_range.
            await self._availability_repository.delete(slot)

        leave_request.request_status = LeaveRequestStatus.APPROVED
        leave_request.approved_by = admin_id
        leave_request.approved_at = _utc_now()
        leave_request.updated_at = _utc_now()
        await self._leave_request_repository.save(leave_request)

        logger.info(
            "Admin %s approved leave request %s: slots_removed=%s appointments_cancelled=%s",
            admin_id, leave_request_id, len(slots_in_range), cancelled_count,
        )
        return leave_request, cancelled_count

    async def _get_doctor_name(self, doctor_id: PydanticObjectId) -> str:
        """
        doctor_id on a LeaveRequest is actually a User id, so the doctor's
        name is looked up from the User collection (no separate doctor
        collection or duplicated name field involved).
        """
        user = await self._user_repository.get_by_id(doctor_id)
        return user.full_name if user else "Unknown Doctor"

    async def to_response(self, leave_request: LeaveRequest) -> LeaveRequestResponse:
        doctor_name = await self._get_doctor_name(leave_request.doctor_id)
        return to_leave_request_response(leave_request, doctor_name)

    async def to_response_list(
        self, leave_requests: list[LeaveRequest]
    ) -> list[LeaveRequestResponse]:
        # cache names so the same doctor isn't looked up more than once per list
        name_cache: dict[PydanticObjectId, str] = {}
        responses = []
        for leave_request in leave_requests:
            if leave_request.doctor_id not in name_cache:
                name_cache[leave_request.doctor_id] = await self._get_doctor_name(
                    leave_request.doctor_id
                )
            responses.append(
                to_leave_request_response(leave_request, name_cache[leave_request.doctor_id])
            )
        return responses


def get_leave_request_service(
    leave_request_repository: LeaveRequestRepository = Depends(get_leave_request_repository),
    availability_repository: AvailabilityRepository = Depends(get_availability_repository),
    appointment_repository: AppointmentRepository = Depends(get_appointment_repository),
    doctor_repository: DoctorRepository = Depends(get_doctor_repository),
    user_repository: UserRepository = Depends(get_user_repository),
    notification_service: NotificationService = Depends(get_notification_service),
) -> LeaveRequestService:
    return LeaveRequestService(
        leave_request_repository,
        availability_repository,
        appointment_repository,
        doctor_repository,
        user_repository,
        notification_service,
    )