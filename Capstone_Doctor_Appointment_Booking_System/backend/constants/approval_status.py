"""
ApprovalStatus tracks where a doctor's application is in the admin
review process.
"""

from enum import Enum


class ApprovalStatus(str, Enum):
    PENDING_APPROVAL = "PENDING_APPROVAL"
    APPROVED = "APPROVED"
    REJECTED = "REJECTED"