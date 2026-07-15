"""
Enum representing user roles in the system.
"""
from enum import Enum

class Role(str, Enum):

    PATIENT = "PATIENT"
    DOCTOR = "DOCTOR"
    ADMIN = "ADMIN"
