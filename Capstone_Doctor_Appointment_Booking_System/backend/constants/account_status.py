"""
AccountStatus controls one thing only: whether a user's account is currently usable
(able to log in, and for doctors, searchable/bookable).
"""

from enum import Enum


class AccountStatus(str, Enum):
    ACTIVE = "ACTIVE"
    INACTIVE = "INACTIVE"