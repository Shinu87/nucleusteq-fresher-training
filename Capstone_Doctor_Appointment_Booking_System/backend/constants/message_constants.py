"""
Application message constants.
"""


class GeneralMessages:
    INTERNAL_SERVER_ERROR = "An unexpected error occurred. Please try again later."
    VALIDATION_FAILED = "One or more fields failed validation."
    FIELD_CANNOT_BE_BLANK = "This field cannot be blank"


class AuthMessages:
    INVALID_CREDENTIALS = "Invalid email or password"
    ACCOUNT_PENDING_APPROVAL = "Your application is still pending admin approval."
    ACCOUNT_REJECTED = "Your application was rejected. Please contact support."
    ACCOUNT_INACTIVE = "This account is not active. Please contact support."
    TOKEN_EXPIRED = "Your session has expired. Please log in again."
    INVALID_TOKEN = "Invalid authentication token."
    SETUP_LINK_INVALID = "This setup link is invalid or has already been used"
    SETUP_LINK_EXPIRED = (
        "This setup link has expired. Please ask an admin to re-approve your application."
    )
    INSUFFICIENT_ROLE = "This action requires one of these roles: {roles}"


class UserMessages:
    EMAIL_ALREADY_REGISTERED = "Email is already registered"
    USER_NOT_FOUND = "User not found"
    PATIENT_NOT_FOUND = "Patient not found"


class DoctorMessages:
    DOCTOR_NOT_FOUND = "Doctor not found"
    DOCTOR_PROFILE_SYNC_MISSING = (
        "Your doctor profile was not found in Appointment Service. "
        "Please contact an admin - the approval sync may have failed."
    )
    DOCTOR_INACTIVE_OR_NOT_FOUND = "The doctor for this slot was not found or is no longer active"
    LICENSE_ALREADY_REGISTERED = "This license number is already registered"
    APPLICATION_NOT_FOUND = "Doctor application not found"
    LINKED_USER_NOT_FOUND = "Linked user account not found"
    APPLICATION_ALREADY_REVIEWED = "This application has already been {status}"


class SlotMessages:
    SLOT_NOT_FOUND = "Slot not found"
    SLOT_OWNERSHIP_VIOLATION = "You can only manage your own availability slots"
    SLOT_NOT_EDITABLE = "A booked slot cannot be edited"
    SLOT_NOT_DELETABLE = "A booked slot cannot be deleted"
    DUPLICATE_SLOT = "You already have a slot starting at this date and time"
    INVALID_TIME_RANGE = "end_time must be after start_time"


class AppointmentMessages:
    SLOT_IN_PAST = "Cannot book a slot that is already in the past"
    SLOT_UNAVAILABLE = "This slot has already been booked. Please choose another slot."


class EmailMessages:
    SETUP_PASSWORD_SUBJECT = "Doctor Account Approved"
