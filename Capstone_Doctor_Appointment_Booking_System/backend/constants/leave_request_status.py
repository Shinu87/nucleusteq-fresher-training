"""
LeaveRequestStatus tracks a doctor's emergency leave
request through the admin review workflow.
"""

from enum import Enum


class LeaveRequestStatus(str, Enum):
    PENDING = "PENDING"
    APPROVED = "APPROVED"
    REJECTED = "REJECTED"
