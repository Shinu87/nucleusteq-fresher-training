"""
Request schemas for a doctor's emergency leave request.
"""

from datetime import date

from pydantic import BaseModel, field_validator, model_validator

from backend.constants.validation_constants import ValidationMessages, ValidationPatterns


class LeaveRequestCreate(BaseModel):

    date: date
    start_time: str
    end_time: str
    reason: str

    @field_validator("start_time", "end_time")
    @classmethod
    def validate_time_format(cls, value: str) -> str:
        if not ValidationPatterns.TIME_REGEX.match(value):
            raise ValueError(ValidationMessages.TIME_FORMAT_INVALID)
        return value

    @field_validator("date")
    @classmethod
    def validate_not_in_the_past(cls, value: date) -> date:
        if value < date.today():
            raise ValueError(ValidationMessages.SLOT_DATE_IN_PAST)
        return value

    @field_validator("reason")
    @classmethod
    def validate_reason_not_blank(cls, value: str) -> str:
        value = value.strip()
        if not value:
            raise ValueError(ValidationMessages.FIELD_CANNOT_BE_BLANK)
        return value

    @model_validator(mode="after")
    def validate_end_after_start(self) -> "LeaveRequestCreate":
        if self.end_time <= self.start_time:
            raise ValueError(ValidationMessages.END_TIME_BEFORE_START)
        return self


class RejectLeaveRequest(BaseModel):

    rejection_reason: str

    @field_validator("rejection_reason")
    @classmethod
    def validate_reason_not_blank(cls, value: str) -> str:
        value = value.strip()
        if not value:
            raise ValueError(ValidationMessages.FIELD_CANNOT_BE_BLANK)
        return value
