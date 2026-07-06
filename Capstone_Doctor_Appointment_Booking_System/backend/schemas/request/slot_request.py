"""
Request schemas for managing a doctor's availability slots.
"""

import re
from datetime import date
from typing import Optional

from pydantic import BaseModel, Field,field_validator, model_validator
from backend.constants.validation_constants import ValidationMessages, ValidationPatterns, ValidationLimits

# matches a 24-hour HH:MM time like "09:00" or "17:30"

class CreateSlotRequest(BaseModel):
    slot_date: date
    start_time: str
    end_time: str

    @field_validator("start_time", "end_time")
    @classmethod
    def validate_time_format(cls, value: str) -> str:
        if not ValidationPatterns.TIME_REGEX.match(value):
            raise ValueError(ValidationMessages.TIME_FORMAT_INVALID)
        return value

    @field_validator("slot_date")
    @classmethod
    def validate_not_in_the_past(cls, value: date) -> date:
        if value < date.today():
            raise ValueError(ValidationMessages.SLOT_DATE_IN_PAST)
        return value

    @model_validator(mode="after")
    def validate_end_after_start(self) -> "CreateSlotRequest":
        if self.end_time <= self.start_time:
            raise ValueError(ValidationMessages.END_TIME_BEFORE_START)
        return self


class UpdateSlotRequest(BaseModel):
    """
    All fields optional, so a doctor can update just the piece they want
    to change.
    """

    slot_date: Optional[date] = None
    start_time: Optional[str] = None
    end_time: Optional[str] = None

    @field_validator("start_time", "end_time")
    @classmethod
    def validate_time_format(cls, value: Optional[str]) -> Optional[str]:
        if value is not None and not ValidationPatterns.TIME_REGEX.match(value):
            raise ValueError(ValidationMessages.TIME_FORMAT_INVALID)
        return value

    @field_validator("slot_date")
    @classmethod
    def validate_not_in_the_past(cls, value: Optional[date]) -> Optional[date]:
        if value is not None and value < date.today():
            raise ValueError(ValidationMessages.SLOT_DATE_IN_PAST)
        return value
    
class GenerateSlotsRequest(BaseModel):

    slot_date: date
    start_time: str
    end_time: str
    duration_minutes: int = Field(
        ...,
        ge=ValidationLimits.MIN_SLOT_DURATION_MINUTES,
        le=ValidationLimits.MAX_SLOT_DURATION_MINUTES,
    )

    @field_validator("start_time", "end_time")
    @classmethod
    def validate_time_format(cls, value: str) -> str:
        if not ValidationPatterns.TIME_REGEX.match(value):
            raise ValueError(ValidationMessages.TIME_FORMAT_INVALID)
        return value

    @field_validator("slot_date")
    @classmethod
    def validate_not_in_the_past(cls, value: date) -> date:
        if value < date.today():
            raise ValueError(ValidationMessages.SLOT_DATE_IN_PAST)
        return value

    @model_validator(mode="after")
    def validate_end_after_start(self) -> "GenerateSlotsRequest":
        if self.end_time <= self.start_time:
            raise ValueError(ValidationMessages.END_TIME_BEFORE_START)
        return self


class BlockRangeRequest(BaseModel):
    """
    Doctor selects a date + time range; every still-AVAILABLE slot whose
    start_time falls inside [start_time, end_time) is removed.
    """

    slot_date: date
    start_time: str
    end_time: str

    @field_validator("start_time", "end_time")
    @classmethod
    def validate_time_format(cls, value: str) -> str:
        if not ValidationPatterns.TIME_REGEX.match(value):
            raise ValueError(ValidationMessages.TIME_FORMAT_INVALID)
        return value

    @field_validator("slot_date")
    @classmethod
    def validate_not_in_the_past(cls, value: date) -> date:
        if value < date.today():
            raise ValueError(ValidationMessages.SLOT_DATE_IN_PAST)
        return value

    @model_validator(mode="after")
    def validate_end_after_start(self) -> "BlockRangeRequest":
        if self.end_time <= self.start_time:
            raise ValueError(ValidationMessages.END_TIME_BEFORE_START)
        return self