"""
ApprovalStatus tracks where a doctor's application is in the admin
review process. This lives on the doctor_profiles document, separate
from the user's AccountStatus
(an approved doctor still can't log in until they've set a password).
"""

from enum import Enum


class ApprovalStatus(str, Enum):
    PENDING_APPROVAL = "PENDING_APPROVAL"
    APPROVED = "APPROVED"
    REJECTED = "REJECTED"