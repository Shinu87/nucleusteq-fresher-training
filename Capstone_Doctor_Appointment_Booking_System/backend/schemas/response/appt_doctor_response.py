"""
Response schemas for the patient-facing doctor search and detail pages.
"""

from datetime import date

from pydantic import BaseModel


class DoctorSummaryResponse(BaseModel):
    id: str
    full_name: str
    specialization: str
    qualification: str
    experience_years: int
    consultation_fee: float
    clinic_address: str


class AvailableSlotSummary(BaseModel):
    id: str
    slot_date: date
    start_time: str
    end_time: str


class DoctorDetailResponse(DoctorSummaryResponse):
    available_slots: list[AvailableSlotSummary]
