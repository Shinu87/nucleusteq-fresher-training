"""
Constants used when building outbound emails (subjects, link paths).
"""


class EmailSubjects:
    DOCTOR_ACCOUNT_APPROVED = "Doctor Account Approved"


class EmailLinkPaths:
    # appended to settings.frontend_base_url, e.g. http://localhost:3000/set-password/<token>
    SET_PASSWORD = "/set-password/{token}"
