"""
API-level constants: router prefixes and OpenAPI tag names.
"""


class APIPrefixes:
    AUTH = "/auth"
    ADMIN = "/admin"
    AVAILABILITY_SLOTS = "/doctor/availability/slots"
    DOCTORS = "/doctors"
    APPOINTMENTS = "/appointments"
    DOCTOR_SELF_SERVICE = "/doctor"


class APITags:
    AUTHENTICATION = "Authentication"
    ADMIN_DOCTOR_APPROVAL = "Admin - Doctor Approval"
    AVAILABILITY_SLOTS = "Availability Slots"
    DOCTOR_SEARCH = "Doctor Search"
    APPOINTMENTS = "Appointments"
    HEALTH = "Health"
    DOCTOR_SELF_SERVICE = "Doctor - Account & Leave"
    ADMIN_LEAVE_REQUESTS = "Admin - Leave Requests"

class TokenType:
    BEARER = "bearer"
