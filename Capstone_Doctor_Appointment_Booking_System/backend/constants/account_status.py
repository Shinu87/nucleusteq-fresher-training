"""
AccountStatus controls whether a user is allowed to log in at all.
"""

from enum import Enum


class AccountStatus(str, Enum):
    ACTIVE = "ACTIVE"
    PENDING_APPROVAL = "PENDING_APPROVAL"
    REJECTED = "REJECTED"
    DEACTIVATED = "DEACTIVATED"