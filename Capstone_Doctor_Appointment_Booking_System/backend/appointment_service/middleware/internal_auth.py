"""
Service-to-service authentication for Appointment Service's /internal/*
routes.

These routes are NEVER called by a logged-in person's browser - only by
User Service's backend code. So instead of checking a user's JWT, I
check a shared secret header (X-Internal-Key) that only other services
know.
"""

from fastapi import Header, HTTPException, status

from appointment_service.config import get_settings

settings = get_settings()


async def require_internal_api_key(x_internal_key: str = Header(...)) -> None:
    if x_internal_key != settings.internal_api_key:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid or missing internal API key",
        )