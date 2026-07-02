"""
Business logic for actually sending a notification.
"""

import logging

from backend.models.notification import (
    Notification,
    NotificationStatus,
    NotificationType,
)
from backend.schemas.request.internal_request import SendNotificationRequest
from backend.utils.mailer import build_setup_password_email_html, send_email

logger = logging.getLogger(__name__)


async def send_notification(payload: SendNotificationRequest) -> Notification:
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
                subject="Doctor Account Approved",
                html_body=html_body,
            )
            logger.info("Setup password email sent to %s", payload.recipient_email)
        except Exception as error:
            # If Gmail SMTP fails, we don't crash the request. We mark the notification as FAILED
            # and continue. The user still gets a normal response.
            logger.error(
                "Failed to send setup password email to %s: %s", payload.recipient_email, error
            )
            notification.status = NotificationStatus.FAILED
    else:
        # no real email template for these yet - just record like before
        logger.info(
            "Notification recorded (not yet wired to a real email) -> to=%s type=%s payload=%s",
            payload.recipient_email,
            payload.type.value,
            payload.payload,
        )

    await notification.insert()
    return notification