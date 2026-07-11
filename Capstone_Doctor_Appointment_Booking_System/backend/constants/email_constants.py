"""
Constants used when building outbound emails (subjects, link paths).
"""


class EmailSubjects:
    DOCTOR_ACCOUNT_APPROVED = "Doctor Account Approved"
    APPOINTMENT_CANCELLED = "Your Appointment Has Been Cancelled"

class EmailLinkPaths:
    SET_PASSWORD = "/set-password/{token}"
