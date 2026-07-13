from types import SimpleNamespace

APIPrefixes = SimpleNamespace(
    AUTH="/auth",
    ADMIN="/admin",
    AVAILABILITY_SLOTS="/doctor/availability/slots",
    DOCTORS="/doctors",
    APPOINTMENTS="/appointments",
    DOCTOR_SELF_SERVICE="/doctor",
)

APITags = SimpleNamespace(
    AUTHENTICATION="Authentication",
    ADMIN_DOCTOR_APPROVAL="Admin - Doctor Approval",
    AVAILABILITY_SLOTS="Availability Slots",
    DOCTOR_SEARCH="Doctor Search",
    APPOINTMENTS="Appointments",
    HEALTH="Health",
    DOCTOR_SELF_SERVICE="Doctor - Account & Leave",
    ADMIN_LEAVE_REQUESTS="Admin - Leave Requests",
)

TokenType = SimpleNamespace(
    BEARER="bearer",
)