"""
Response schema for a booked (or viewed) appointment.
"""

from datetime import date, datetime
from typing import Optional

from pydantic import BaseModel

from backend.constants.appointment_status import AppointmentStatus, PaymentStatus


class AppointmentResponse(BaseModel):
    id: str
    patient_id: str
    patient_name: str
    doctor_id: str
    doctor_name: str
    slot_id: str
    appointment_date: date
    start_time: str
    end_time: str
    status: AppointmentStatus
    payment_status: PaymentStatus
    booked_at: datetime
    cancelled_at: Optional[datetime] = None
    completed_at: Optional[datetime] = None