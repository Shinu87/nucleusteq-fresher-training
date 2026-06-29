"""
AccountStatus controls whether a user is allowed to log in at all.

A PATIENT or ADMIN account is ACTIVE from the moment it's created. A
DOCTOR account starts out PENDING_APPROVAL and only becomes ACTIVE after
an admin approves them AND they finish setting their password.
"""

from enum import Enum


class AccountStatus(str, Enum):
    ACTIVE = "ACTIVE"
    PENDING_APPROVAL = "PENDING_APPROVAL"
    REJECTED = "REJECTED"
    DEACTIVATED = "DEACTIVATED"