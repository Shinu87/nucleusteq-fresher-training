"""
Response schemas for the patient-facing doctor search and detail pages.
"""

from datetime import date

from pydantic import BaseModel


class DoctorSummaryResponse(BaseModel):
    """One row in the doctor search/listing results."""

    id: str
    full_name: str
    specialization: str
    qualification: str
    experience_years: int
    consultation_fee: float
    clinic_address: str


class AvailableSlotSummary(BaseModel):
    """
    A trimmed-down slot shown on a doctor's profile page - a patient
    just needs to see when a slot is.
    """

    id: str
    slot_date: date
    start_time: str
    end_time: str


class DoctorDetailResponse(DoctorSummaryResponse):
    """Everything from the summary, plus this doctor's upcoming available slots."""

    available_slots: list[AvailableSlotSummary]