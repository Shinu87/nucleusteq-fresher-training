"""
Request schemas used inside the application.
"""

from typing import Any, Dict

from pydantic import BaseModel, EmailStr

from backend.constants.specialization import Specialization
from backend.models.notification import NotificationType


class DoctorSyncRequest(BaseModel):

    doctor_id: str
    full_name: str
    specialization: Specialization
    qualification: str
    experience_years: int
    consultation_fee: float
    clinic_address: str
    is_active: bool = True


class SendNotificationRequest(BaseModel):

    recipient_email: EmailStr
    type: NotificationType
    payload: Dict[str, Any] = {}
