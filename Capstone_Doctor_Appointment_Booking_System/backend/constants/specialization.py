"""
Enum representing the doctor specializations supported by the platform.
"""
from enum import Enum


class Specialization(str, Enum):
    CARDIOLOGY = "Cardiology"
    DERMATOLOGY = "Dermatology"
    NEUROLOGY = "Neurology"
    ORTHOPEDICS = "Orthopedics"
    PEDIATRICS = "Pediatrics"
    GENERAL_PHYSICIAN = "General Physician"
    GYNECOLOGY = "Gynecology"
    PSYCHIATRY = "Psychiatry"
    OPHTHALMOLOGY = "Ophthalmology"
    ENT = "ENT"
