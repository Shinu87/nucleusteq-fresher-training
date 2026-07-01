"""
HTTP client for the calls User Service makes to Notification Service.
"""

import logging

import httpx

from user_service.config import get_settings

logger = logging.getLogger(__name__)
settings = get_settings()


async def send_setup_password_notification(recipient_email: str, raw_setup_token: str) -> None:
    """
    Asks Notification Service to "send" the doctor's setup-password email.

    Just like the Appointment Service sync, a failure here is logged and
    does not roll back the admin's approve action.
    """
    setup_link = f"http://localhost:3000/set-password/{raw_setup_token}"

    payload = {
        "recipient_email": recipient_email,
        "type": "SETUP_PASSWORD",
        "payload": {"setup_link": setup_link},
    }

    url = f"{settings.notification_service_url}/internal/notifications/send"
    headers = {"X-Internal-Key": settings.internal_api_key}

    try:
        async with httpx.AsyncClient(timeout=5.0) as client:
            response = await client.post(url, json=payload, headers=headers)
            response.raise_for_status()
        logger.info("Requested setup-password notification for %s", recipient_email)
    except httpx.HTTPError as error:
        logger.error(
            "Failed to request setup-password notification for %s: %s", recipient_email, error
        )