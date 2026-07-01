"""
Internal-only routes for User Service
"""

from beanie import PydanticObjectId
from fastapi import APIRouter, Depends, HTTPException, status
from pydantic import BaseModel

from user_service.middleware.internal_auth import require_internal_api_key
from user_service.models.user import User

router = APIRouter(
    prefix="/internal",
    tags=["Internal - Service to Service"],
    dependencies=[Depends(require_internal_api_key)],
)


class InternalUserResponse(BaseModel):
    """
    Minimal user fields that Appointment Service is allowed to see.
    """

    id: str
    full_name: str
    email: str
    phone_number: str


@router.get("/users/{user_id}", response_model=InternalUserResponse)
async def get_user_by_id(user_id: PydanticObjectId):
    """
    Called by Appointment Service at booking time to fetch the patient's
    name and phone number so they can be denormalized onto the Appointment
    document.
    """
    user = await User.get(user_id)
    if user is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User not found")

    return InternalUserResponse(
        id=str(user.id),
        full_name=user.full_name,
        email=user.email,
        phone_number=user.phone_number,
    )