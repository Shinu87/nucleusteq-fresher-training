"""
API-level constants: router prefixes and OpenAPI tag names.
"""


class APIPrefixes:
    AUTH = "/auth"
    ADMIN = "/admin"
    AVAILABILITY_SLOTS = "/doctors/me/slots"
    DOCTORS = "/doctors"
    APPOINTMENTS = "/appointments"


class APITags:
    AUTHENTICATION = "Authentication"
    ADMIN_DOCTOR_APPROVAL = "Admin - Doctor Approval"
    AVAILABILITY_SLOTS = "Availability Slots"
    DOCTOR_SEARCH = "Doctor Search"
    APPOINTMENTS = "Appointments"
    HEALTH = "Health"


class TokenType:
    BEARER = "bearer"
