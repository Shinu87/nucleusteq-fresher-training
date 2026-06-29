"""
Request schemas for managing a doctor's availability slots.
"""

import re
from datetime import date
from typing import Optional

from pydantic import BaseModel, field_validator, model_validator

# matches a 24-hour HH:MM time like "09:00" or "17:30"
TIME_REGEX = re.compile(r"^([01]\d|2[0-3]):([0-5]\d)$")


class CreateSlotRequest(BaseModel):
    slot_date: date
    start_time: str
    end_time: str

    @field_validator("start_time", "end_time")
    @classmethod
    def validate_time_format(cls, value: str) -> str:
        if not TIME_REGEX.match(value):
            raise ValueError("Time must be in 24-hour HH:MM format, e.g. '09:30'")
        return value

    @field_validator("slot_date")
    @classmethod
    def validate_not_in_the_past(cls, value: date) -> date:
        if value < date.today():
            raise ValueError("slot_date cannot be in the past")
        return value

    @model_validator(mode="after")
    def validate_end_after_start(self) -> "CreateSlotRequest":
        if self.end_time <= self.start_time:
            raise ValueError("end_time must be after start_time")
        return self


class UpdateSlotRequest(BaseModel):
    """
    All fields optional, so a doctor can update just the piece they want
    to change. Only AVAILABLE slots can be updated (enforced in the
    service layer, not here) - editing a slot a patient already booked
    into would leave that patient's appointment showing the wrong time.
    """

    slot_date: Optional[date] = None
    start_time: Optional[str] = None
    end_time: Optional[str] = None

    @field_validator("start_time", "end_time")
    @classmethod
    def validate_time_format(cls, value: Optional[str]) -> Optional[str]:
        if value is not None and not TIME_REGEX.match(value):
            raise ValueError("Time must be in 24-hour HH:MM format, e.g. '09:30'")
        return value

    @field_validator("slot_date")
    @classmethod
    def validate_not_in_the_past(cls, value: Optional[date]) -> Optional[date]:
        if value is not None and value < date.today():
            raise ValueError("slot_date cannot be in the past")
        return value