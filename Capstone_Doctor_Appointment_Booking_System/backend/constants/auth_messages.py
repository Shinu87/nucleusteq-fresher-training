"""
Authentication messages.
"""


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