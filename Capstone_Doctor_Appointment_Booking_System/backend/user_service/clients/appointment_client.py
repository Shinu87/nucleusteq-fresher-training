"""
HTTP client for the one call User Service makes to Appointment

This is plain REST over httpx - no message queue, no shared database.
Authentication uses the shared INTERNAL_API_KEY header rather than a
user's JWT, since this call isn't coming from a logged-in person, it's
coming from another service.
"""

import logging

import httpx

from user_service.config import get_settings
from user_service.models.doctor_profile import DoctorProfile
from user_service.models.user import User

logger = logging.getLogger(__name__)
settings = get_settings()


async def sync_doctor_to_appointment_service(user: User, profile: DoctorProfile) -> None:
    """
    Calls Appointment Service's internal endpoint so the newly-approved
    doctor becomes visible there too.

    If this call fails, we log it but do NOT block the approval itself -
    the admin's "approve" action already succeeded in User Service, and
    a failed sync can be retried later without re-doing the
    approval. This keeps the user-facing approve action fast and simple.
    """
    payload = {
        "doctor_id": str(user.id),
        "full_name": user.full_name,
        "specialization": profile.specialization,
        "qualification": profile.qualification,
        "experience_years": profile.experience_years,
        "consultation_fee": profile.consultation_fee,
        "clinic_address": profile.clinic_address,
        "is_active": user.is_active,
    }

    url = f"{settings.appointment_service_url}/internal/doctors"
    headers = {"X-Internal-Key": settings.internal_api_key}

    try:
        async with httpx.AsyncClient(timeout=5.0) as client:
            response = await client.post(url, json=payload, headers=headers)
            response.raise_for_status()
        logger.info("Synced doctor %s to Appointment Service", user.email)
    except httpx.HTTPError as error:
        logger.error("Failed to sync doctor %s to Appointment Service: %s", user.email, error)