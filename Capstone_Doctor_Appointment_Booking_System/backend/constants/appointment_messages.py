"""
Appointment messages.
"""


class AppointmentMessages:
    SLOT_IN_PAST = "Cannot book a slot that is already in the past"
    SLOT_UNAVAILABLE = "This slot has already been booked. Please choose another slot."
    APPOINTMENT_OWNERSHIP_VIOLATION = "You can only manage your own appointments"
    NOT_CANCELLABLE = "Only a BOOKED appointment can be cancelled"
    CANCELLATION_WINDOW_PASSED = (
        "Appointments can only be cancelled at least {hours} hour(s) "
        "before the scheduled time"
    )
    NOT_COMPLETABLE = "Only a BOOKED appointment can be marked as completed or no-show"
    TIME_NOT_PASSED = (
        "Cannot mark an appointment as completed or no-show before its "
        "scheduled time has passed"
    )