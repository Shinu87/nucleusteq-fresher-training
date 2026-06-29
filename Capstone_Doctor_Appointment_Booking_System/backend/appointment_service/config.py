"""
Centralized configuration for Appointment Service.

This is its OWN settings object, separate from User Service's - true
microservices don't share a config module, even though several of the
values (JWT secret, internal API key) happen to have the same value
across all three services' .env files.
"""

from functools import lru_cache
from typing import List

from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    app_name: str = Field(default="Doctor Appointment Booking System - Appointment Service")
    app_env: str = Field(default="development")
    api_v1_prefix: str = Field(default="/api/v1")

    mongo_uri: str = Field(default="mongodb://localhost:27017")
    mongo_db_name: str = Field(default="doctor_booking_db_appointments")

    jwt_secret_key: str = Field(default="asdfghjkl")
    jwt_algorithm: str = Field(default="HS256")

    internal_api_key: str = Field(default="internal-dev-key")

    cors_origins: List[str] = Field(default=["http://localhost:3000"])

    model_config = SettingsConfigDict(
        env_file="appointment_service/.env",
        env_file_encoding="utf-8",
        case_sensitive=False,
        extra="ignore",
    )


@lru_cache
def get_settings() -> Settings:
    return Settings()