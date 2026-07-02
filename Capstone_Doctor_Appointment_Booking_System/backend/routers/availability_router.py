"""
Routes for a doctor managing their own availability slots.

All four routes here are DOCTOR-only (require_role(Role.DOCTOR)) - a
patient or admin token gets a 403.
"""

from beanie import PydanticObjectId
from fastapi import APIRouter, Depends, status

from backend.constants.roles import Role
from backend.middleware.auth import CurrentUser, require_role
from backend.schemas.request.slot_request import CreateSlotRequest, UpdateSlotRequest
from backend.schemas.response.slot_response import SlotResponse
from backend.services import availability_service

router = APIRouter(prefix="/doctors/me/slots", tags=["Availability Slots"])


def _to_slot_response(slot) -> SlotResponse:
    return SlotResponse(
        id=str(slot.id),
        doctor_id=str(slot.doctor_id),
        slot_date=slot.slot_date,
        start_time=slot.start_time,
        end_time=slot.end_time,
        status=slot.status,
        created_at=slot.created_at,
    )


@router.post("", response_model=SlotResponse, status_code=status.HTTP_201_CREATED)
async def create_slot(
    payload: CreateSlotRequest,
    current_user: CurrentUser = Depends(require_role(Role.DOCTOR)),
):
    slot = await availability_service.create_slot(PydanticObjectId(current_user.id), payload)
    return _to_slot_response(slot)


@router.get("", response_model=list[SlotResponse])
async def list_my_slots(
    current_user: CurrentUser = Depends(require_role(Role.DOCTOR)),
):
    slots = await availability_service.list_my_slots(PydanticObjectId(current_user.id))
    return [_to_slot_response(slot) for slot in slots]


@router.patch("/{slot_id}", response_model=SlotResponse)
async def update_slot(
    slot_id: PydanticObjectId,
    payload: UpdateSlotRequest,
    current_user: CurrentUser = Depends(require_role(Role.DOCTOR)),
):
    slot = await availability_service.update_slot(
        PydanticObjectId(current_user.id), slot_id, payload
    )
    return _to_slot_response(slot)


@router.delete("/{slot_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_slot(
    slot_id: PydanticObjectId,
    current_user: CurrentUser = Depends(require_role(Role.DOCTOR)),
):
    await availability_service.delete_slot(PydanticObjectId(current_user.id), slot_id)