"""
Routes for a doctor managing their own availability slots.
"""

from beanie import PydanticObjectId
from fastapi import APIRouter, Depends, status

from backend.constants.api_constants import APIPrefixes, APITags
from backend.constants.roles import Role
from backend.middleware.auth import CurrentUser, require_role
from backend.schemas.request.slot_request import (
    BlockRangeRequest,
    CreateSlotRequest,
    GenerateSlotsRequest,
    UpdateSlotRequest,
)
from backend.schemas.response.slot_response import (
    BlockRangeResponse,
    GenerateSlotsResponse,
    SlotResponse,
)
from backend.services.availability_service import (
    AvailabilityService,
    get_availability_service,
)
from backend.utils.slot_mapper import to_slot_response
router = APIRouter(prefix=APIPrefixes.AVAILABILITY_SLOTS, tags=[APITags.AVAILABILITY_SLOTS])


@router.post("", response_model=SlotResponse, status_code=status.HTTP_201_CREATED)
async def create_slot(
    payload: CreateSlotRequest,
    current_user: CurrentUser = Depends(require_role(Role.DOCTOR)),
    availability_service: AvailabilityService = Depends(get_availability_service)
):
    slot = await availability_service.create_slot(PydanticObjectId(current_user.id), payload)
    return to_slot_response(slot)


@router.post("/generate", response_model=GenerateSlotsResponse, status_code=status.HTTP_201_CREATED)
async def generate_slots(
    payload: GenerateSlotsRequest,
    current_user: CurrentUser = Depends(require_role(Role.DOCTOR)),
    availability_service: AvailabilityService = Depends(get_availability_service)
):
    created, skipped, total_requested = await availability_service.generate_slots(
        PydanticObjectId(current_user.id), payload
    )
    return GenerateSlotsResponse(
        total_slots_requested=total_requested,
        created_count=len(created),
        skipped_count=len(skipped),
        created_slots=[to_slot_response(slot) for slot in created],
        skipped_slots=skipped,
    )


@router.post("/block-range", response_model=BlockRangeResponse, status_code=status.HTTP_200_OK)
async def block_range(
    payload: BlockRangeRequest,
    current_user: CurrentUser = Depends(require_role(Role.DOCTOR)),
    availability_service: AvailabilityService = Depends(get_availability_service)
):
    total_found, blocked, skipped = await availability_service.block_range(
        PydanticObjectId(current_user.id), payload
    )
    return BlockRangeResponse(
        total_slots_found=total_found,
        blocked_count=len(blocked),
        skipped_booked_count=len(skipped),
        blocked_slots=blocked,
        skipped_slots=skipped,
    )


@router.get("", response_model=list[SlotResponse])
async def list_my_slots(
    current_user: CurrentUser = Depends(require_role(Role.DOCTOR)),
    availability_service: AvailabilityService = Depends(get_availability_service)
):
    slots = await availability_service.list_my_slots(PydanticObjectId(current_user.id))
    return [to_slot_response(slot) for slot in slots]


@router.patch("/{slot_id}", response_model=SlotResponse)
async def update_slot(
    slot_id: PydanticObjectId,
    payload: UpdateSlotRequest,
    current_user: CurrentUser = Depends(require_role(Role.DOCTOR)),
    availability_service: AvailabilityService = Depends(get_availability_service)
):
    slot = await availability_service.update_slot(
        PydanticObjectId(current_user.id), slot_id, payload
    )
    return to_slot_response(slot)


@router.delete("/{slot_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_slot(
    slot_id: PydanticObjectId,
    current_user: CurrentUser = Depends(require_role(Role.DOCTOR)),
    availability_service: AvailabilityService = Depends(get_availability_service)
):
    await availability_service.delete_slot(PydanticObjectId(current_user.id), slot_id)