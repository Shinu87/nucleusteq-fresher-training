"""
Request schema for booking an appointment.
"""

from pydantic import BaseModel


class BookAppointmentRequest(BaseModel):

    slot_id: str