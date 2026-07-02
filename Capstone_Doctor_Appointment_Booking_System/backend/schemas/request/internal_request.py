"""
Request schemas used inside the application.
"""

from typing import Any, Dict

from pydantic import BaseModel, EmailStr

from backend.models.notification import NotificationType


class DoctorSyncRequest(BaseModel):
    """Used by doctor_profile_service to populate the Doctor search model."""

    doctor_id: str
    full_name: str
    specialization: str
    qualification: str
    experience_years: int
    consultation_fee: float
    clinic_address: str
    is_active: bool = True


class SendNotificationRequest(BaseModel):
    """Used to trigger email sending via notification_service.send_notification()."""

    recipient_email: EmailStr
    type: NotificationType
    payload: Dict[str, Any] = {}
