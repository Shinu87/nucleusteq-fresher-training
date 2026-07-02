"""
AppointmentStatus tracks the lifecycle of a patient's appointment.
"""

from enum import Enum


class AppointmentStatus(str, Enum):
    BOOKED = "BOOKED"
    CANCELLED = "CANCELLED"
    COMPLETED = "COMPLETED"
    NO_SHOW = "NO_SHOW"


class PaymentStatus(str, Enum):
    MOCK_PAID = "MOCK_PAID"