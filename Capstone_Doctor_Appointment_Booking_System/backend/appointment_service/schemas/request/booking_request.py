"""
Request schema for booking an appointment.
"""

from pydantic import BaseModel


class BookAppointmentRequest(BaseModel):
    """
    The patient only needs to tell us which slot they want. 
    """
    slot_id: str