"""
Enum representing a patient's gender.
"""

from enum import Enum


class Gender(str, Enum):

    MALE = "MALE"
    FEMALE = "FEMALE"
    OTHER = "OTHER"
