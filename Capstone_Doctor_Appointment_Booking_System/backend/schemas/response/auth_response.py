"""
Response schema for the registration endpoints.
"""

from datetime import date, datetime
from typing import Optional

from pydantic import BaseModel

from backend.constants.roles import Role


class UserProfileResponse(BaseModel):
    id: str
    full_name: str
    email: str
    phone_number: str
    role: Role
    gender: Optional[str] = None
    date_of_birth: Optional[date] = None
    is_active: bool
    created_at: datetime

class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    expires_in: int  # seconds until the token expires
    user: UserProfileResponse