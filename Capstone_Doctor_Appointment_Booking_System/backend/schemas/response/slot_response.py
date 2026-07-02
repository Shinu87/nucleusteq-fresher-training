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