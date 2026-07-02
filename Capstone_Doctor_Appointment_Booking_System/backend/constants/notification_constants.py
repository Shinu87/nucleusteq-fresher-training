from enum import Enum


class NotificationType(str, Enum):
    SETUP_PASSWORD = "SETUP_PASSWORD"
    APPOINTMENT_CONFIRMATION = "APPOINTMENT_CONFIRMATION"
    APPOINTMENT_CANCELLATION = "APPOINTMENT_CANCELLATION"


class NotificationStatus(str, Enum):
    SENT = "SENT"
    FAILED = "FAILED"