"""
Fetches patient identity fields from User Service at booking time.
"""

import logging

import httpx
from fastapi import HTTPException, status

from appointment_service.config import get_settings

logger = logging.getLogger(__name__)
settings = get_settings()


async def get_patient_info(user_id: str) -> dict:
    """
    Returns details for the given user.
    Raises 503 if User Service is unreachable, so the caller (the
    booking endpoint) can return a clear error instead of crashing.
    """
    url = f"{settings.user_service_url}/internal/users/{user_id}"
    headers = {"X-Internal-Key": settings.internal_api_key}

    try:
        async with httpx.AsyncClient(timeout=5.0) as client:
            response = await client.get(url, headers=headers)
            response.raise_for_status()
            return response.json()
    except httpx.HTTPStatusError as error:
        logger.error("User Service returned error for user %s: %s", user_id, error)
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Patient profile not found",
        )
    except httpx.HTTPError as error:
        logger.error("Could not reach User Service: %s", error)
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="Could not reach User Service. Please try again shortly.",
        )