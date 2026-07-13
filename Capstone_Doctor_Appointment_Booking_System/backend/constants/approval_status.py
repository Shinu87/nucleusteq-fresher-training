"""
ApprovalStatus tracks where a doctor's application is in the admin
review process.
"""

from enum import Enum


class ApprovalStatus(str, Enum):
    PENDING = "PENDING"
    APPROVED = "APPROVED"
    REJECTED = "REJECTED"