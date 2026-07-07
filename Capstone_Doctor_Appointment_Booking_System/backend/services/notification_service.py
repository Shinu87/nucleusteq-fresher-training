"""
Business logic for actually sending a notification.
"""

import logging

from fastapi import Depends

from backend.models.notification import (
    Notification,
    NotificationStatus,
    NotificationType,
)
from backend.repositories.notification_repository import (
    NotificationRepository,
    get_notification_repository,
)
from backend.schemas.request.internal_request import SendNotificationRequest
from backend.utils.mailer import build_setup_password_email_html, send_email
from backend.constants.email_constants import EmailSubjects


logger = logging.getLogger(__name__)


class NotificationService:

    def __init__(self, notification_repository: NotificationRepository):
        self._notification_repository = notification_repository

    async def send_notification(self, payload: SendNotificationRequest) -> Notification:
        notification = Notification(
            recipient_email=payload.recipient_email,
            type=payload.type,
            payload=payload.payload,
            status=NotificationStatus.SENT,
        )

        if payload.type == NotificationType.SETUP_PASSWORD:
            setup_link = payload.payload.get("setup_link", "")
            html_body = build_setup_password_email_html(setup_link)

            try:
                await send_email(
                    recipient_email=payload.recipient_email,
                    subject=EmailSubjects.DOCTOR_ACCOUNT_APPROVED,
                    html_body=html_body,
                )
                logger.info("Setup password email sent to %s", payload.recipient_email)
            except Exception as error:
                logger.error(
                    "Failed to send setup password email to %s: %s", payload.recipient_email, error
                )
                notification.status = NotificationStatus.FAILED
        else:
            logger.info(
                "Notification recorded (not yet wired to a real email) -> to=%s type=%s payload=%s",
                payload.recipient_email,
                payload.type.value,
                payload.payload,
            )

        await self._notification_repository.insert(notification)
        return notification


def get_notification_service(
    notification_repository: NotificationRepository = Depends(get_notification_repository),
) -> NotificationService:
    return NotificationService(notification_repository)