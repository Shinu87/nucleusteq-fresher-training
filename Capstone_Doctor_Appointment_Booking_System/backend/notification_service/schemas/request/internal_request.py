"""
Request schema for the payload other services send to "send" a notification.
"""

from typing import Any, Dict

from pydantic import BaseModel, EmailStr

from notification_service.models.notification import NotificationType


class SendNotificationRequest(BaseModel):
    recipient_email: EmailStr
    type: NotificationType
    payload: Dict[str, Any] = {}