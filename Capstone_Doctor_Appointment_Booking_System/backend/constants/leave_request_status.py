"""
LeaveRequestStatus tracks a doctor's emergency leave/cancellation
request through the admin review workflow. This mirrors the same
single-responsibility idea as ApprovalStatus: it only ever describes
the leave request itself, never the doctor's account or the affected
appointments.
"""

from enum import Enum


class LeaveRequestStatus(str, Enum):
    PENDING = "PENDING"
    APPROVED = "APPROVED"
    REJECTED = "REJECTED"
