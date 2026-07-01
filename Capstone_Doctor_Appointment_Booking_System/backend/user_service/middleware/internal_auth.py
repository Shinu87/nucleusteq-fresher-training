"""
Service-to-service authentication for User Service's /internal/* routes.
"""

from fastapi import Header, HTTPException, status

from user_service.config import get_settings

settings = get_settings()


async def require_internal_api_key(x_internal_key: str = Header(...)) -> None:
    if x_internal_key != settings.internal_api_key:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid or missing internal API key",
        )