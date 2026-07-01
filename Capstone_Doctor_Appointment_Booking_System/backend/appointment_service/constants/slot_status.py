"""
SlotStatus tracks whether a doctor's availability slot is still free or
already taken by a patient's booking.
"""

from enum import Enum


class SlotStatus(str, Enum):
    AVAILABLE = "AVAILABLE"
    BOOKED = "BOOKED"