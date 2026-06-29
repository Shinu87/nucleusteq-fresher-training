"""
Internal-only routes for Notification Service.
"""

from fastapi import APIRouter, Depends

from notification_service.middleware.internal_auth import require_internal_api_key
from notification_service.schemas.request.internal_request import SendNotificationRequest
from notification_service.services.notification_service import send_notification

router = APIRouter(
    prefix="/internal",
    tags=["Internal - Service to Service"],
    dependencies=[Depends(require_internal_api_key)],
)


@router.post("/notifications/send")
async def send_notification_route(payload: SendNotificationRequest):
    """
    Called by User Service for the doctor "Set Password" email.
    """
    notification = await send_notification(payload)
    return {"status": "sent", "notification_id": str(notification.id)}