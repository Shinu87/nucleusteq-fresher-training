"""
Centralized application configuration.
"""

from functools import lru_cache
from typing import List
from pathlib import Path


from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict

ENV_FILE_PATH = Path(__file__).resolve().parent / ".env"


class Settings(BaseSettings):

    FRONTEND_URL: str = "http://localhost:3000"

    #  App metadata 
    app_name: str = Field(default="Doctor Appointment Booking System - Notification Service")
    app_env: str = Field(default="development")
    api_v1_prefix: str = Field(default="/api/v1")

    #  MongoDB 
    mongo_uri: str = Field(default="mongodb://localhost:27017")
    mongo_db_name: str = Field(default="doctor_booking_db_notifications")

    #  JWT 
    jwt_secret_key: str = Field(default="asdfghjkl")
    jwt_algorithm: str = Field(default="HS256")
    jwt_expire_minutes: int = Field(default=30)

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
        env_file=ENV_FILE_PATH,
        env_file_encoding="utf-8",
        case_sensitive=False,
        extra="ignore",
    )


@lru_cache
def get_settings() -> Settings:
    return Settings()

