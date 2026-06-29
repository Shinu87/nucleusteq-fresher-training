"""
Same Role enum as User Service's constants/roles.py.

Duplicated on purpose, not imported across services - in true
microservices, each service owns its own copy of small shared concepts
like this instead of reaching into another service's codebase.
"""

from enum import Enum


class Role(str, Enum):
    PATIENT = "PATIENT"
    DOCTOR = "DOCTOR"
    ADMIN = "ADMIN"