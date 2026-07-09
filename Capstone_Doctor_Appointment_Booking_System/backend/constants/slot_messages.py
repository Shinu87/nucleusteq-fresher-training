"""
Availability slot messages.
"""

SLOT_NOT_FOUND = "Slot not found"
SLOT_OWNERSHIP_VIOLATION = "You can only manage your own availability slots"
SLOT_NOT_EDITABLE = "A booked slot cannot be edited"
SLOT_NOT_DELETABLE = "A booked slot cannot be deleted"
DUPLICATE_SLOT = "You already have a slot starting at this date and time"
INVALID_TIME_RANGE = "end_time must be after start_time"
NO_SLOTS_GENERATED = (
    "The selected time range is too short to fit even one slot of the requested duration"
)
