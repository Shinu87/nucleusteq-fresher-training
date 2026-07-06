"""
Response schema for an availability slot.
"""

from datetime import date, datetime

from pydantic import BaseModel

from backend.constants.slot_status import SlotStatus


class SlotResponse(BaseModel):
    id: str
    doctor_id: str
    slot_date: date
    start_time: str
    end_time: str
    status: SlotStatus
    created_at: datetime


class SlotTimeRange(BaseModel):
    start_time: str
    end_time: str


class GenerateSlotsResponse(BaseModel):
    total_slots_requested: int
    created_count: int
    skipped_count: int
    created_slots: list[SlotResponse]
    skipped_slots: list[SlotTimeRange]


class BlockRangeResponse(BaseModel):
    total_slots_found: int
    blocked_count: int
    skipped_booked_count: int
    blocked_slots: list[SlotTimeRange]
    skipped_slots: list[SlotTimeRange]