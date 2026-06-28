"""
Centralized configuration for Notification Service. Its own settings
object, separate from the other two services.
"""

from functools import lru_cache
from typing import List

from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    #  App metadata 
    app_name: str = Field(default="Doctor Appointment Booking System - Notification Service")
    app_env: str = Field(default="development")
    api_v1_prefix: str = Field(default="/api/v1")

    #  MongoDB 
    mongo_uri: str = Field(default="mongodb://localhost:27017")
    mongo_db_name: str = Field(default="doctor_booking_db_notifications")

    #  Service-to-service auth - same value as the other two services' .env 
    internal_api_key: str = Field(default="internal-dev-key")

    #  SMTP (Gmail) - used to actually send the doctor approval email 
    smtp_username: str = Field(default="noreply@example.com")
    smtp_password: str = Field(default="changeme")
    smtp_from: str = Field(default="noreply@example.com")
    smtp_port: int = Field(default=587)
    smtp_server: str = Field(default="smtp.gmail.com")
    smtp_starttls: bool = Field(default=True)
    smtp_ssl_tls: bool = Field(default=False)

    #  CORS
    cors_origins: List[str] = Field(default=["http://localhost:3000"])

    model_config = SettingsConfigDict(
        env_file="notification_service/.env",
        env_file_encoding="utf-8",
        case_sensitive=False,
        extra="ignore",
    )


@lru_cache
def get_settings() -> Settings:
    return Settings()

