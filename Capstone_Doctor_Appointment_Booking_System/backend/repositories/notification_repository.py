"""
Repository layer for the Notification document.
"""

from backend.models.notification import Notification


class NotificationRepository:

    async def insert(self, notification: Notification) -> Notification:
        await notification.insert()
        return notification


def get_notification_repository() -> NotificationRepository:
    return NotificationRepository()