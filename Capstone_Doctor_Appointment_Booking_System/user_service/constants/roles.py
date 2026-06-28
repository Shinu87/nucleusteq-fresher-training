"""
Defined once here so every layer of the app imports the same enum instead of re-declaring role strings 
(and risking a typo like "Doctor" vs "DOCTOR").
"""

from enum import Enum

class Role(str, Enum):
    """
    Enum representing user roles in the system.
    """
    PATIENT = "PATIENT"
    DOCTOR = "DOCTOR"
    ADMIN = "ADMIN"
