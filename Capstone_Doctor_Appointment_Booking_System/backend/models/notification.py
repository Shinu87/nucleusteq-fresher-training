"""
Notification is our record of every "email" the system has ever sent.
"""

from datetime import datetime, timezone
from enum import Enum
from typing import Any, Dict

from beanie import Document
from pydantic import Field
from pymongo import IndexModel
from backend.constants.notification_constants import (
    NotificationStatus,
    NotificationType,
)

def _utc_now() -> datetime:
    return datetime.now(timezone.utc)


class Notification(Document):
    recipient_email: str
    type: NotificationType
    payload: Dict[str, Any] = Field(default_factory=dict)
    status: NotificationStatus = NotificationStatus.SENT
    created_at: datetime = Field(default_factory=_utc_now)

    class Settings:
        name = "notifications"
        indexes = [
            IndexModel("recipient_email"),
            IndexModel("type"),
        ]