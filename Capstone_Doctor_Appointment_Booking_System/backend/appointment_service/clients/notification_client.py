"""
HTTP client for the calls Appointment Service makes to Notification Service.
"""

import logging

import httpx

from appointment_service.config import get_settings

logger = logging.getLogger(__name__)
settings = get_settings()


async def send_booking_confirmation(
    recipient_email: str, patient_name: str, doctor_name: str, appointment_date: str, start_time: str
) -> None:
    payload = {
        "recipient_email": recipient_email,
        "type": "APPOINTMENT_CONFIRMATION",
        "payload": {
            "patient_name": patient_name,
            "doctor_name": doctor_name,
            "appointment_date": appointment_date,
            "start_time": start_time,
        },
    }

    url = f"{settings.notification_service_url}/internal/notifications/send"
    headers = {"X-Internal-Key": settings.internal_api_key}

    try:
        async with httpx.AsyncClient(timeout=5.0) as client:
            response = await client.post(url, json=payload, headers=headers)
            response.raise_for_status()
        logger.info("Booking confirmation notification sent to %s", recipient_email)
    except httpx.HTTPError as error:
        # notification failure must never block or roll back a booking
        logger.error(
            "Failed to send booking confirmation to %s: %s", recipient_email, error
        )
