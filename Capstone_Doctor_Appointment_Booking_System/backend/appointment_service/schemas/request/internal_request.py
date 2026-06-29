"""
Request schema for the payload User Service sends when syncing a doctor.
"""

from pydantic import BaseModel


class DoctorSyncRequest(BaseModel):
    doctor_id: str
    full_name: str
    specialization: str
    qualification: str
    experience_years: int
    consultation_fee: float
    clinic_address: str
    is_active: bool = True