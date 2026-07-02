"""
Response schemas for the patient-facing doctor search and detail pages.
"""

from datetime import date

from pydantic import BaseModel


class DoctorSummaryResponse(BaseModel):
    """Doctor search/listing results."""
    id: str
    full_name: str
    specialization: str
    qualification: str
    experience_years: int
    consultation_fee: float
    clinic_address: str


class AvailableSlotSummary(BaseModel):
    """
    Represents slot details shown on the doctor's profile.
    Only basic information like date and time is returned.
    """
    id: str
    slot_date: date
    start_time: str
    end_time: str


class DoctorDetailResponse(DoctorSummaryResponse):
    """Everything from the summary, plus this doctor's upcoming available slots."""

    available_slots: list[AvailableSlotSummary]
