"""
Centralized application configuration.

Loads all environment-driven settings (database, JWT, CORS, app metadata) into a
single typed Settings object so nothing else in the codebase calls os.environ
directly.
"""

from functools import lru_cache
from typing import List

from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    #  App metadata 
    app_name: str = Field(default="Doctor Appointment Booking System")
    app_env: str = Field(default="development")  # development | testing | production
    api_v1_prefix: str = Field(default="/api/v1")

    #  MongoDB 
    mongo_uri: str = Field(default="mongodb://localhost:27017")
    mongo_db_name: str = Field(default="doctor_appointment_db")

    #  JWT 
    jwt_secret_key: str = Field(default="asdfghjkl")
    jwt_algorithm: str = Field(default="HS256")
    jwt_expire_minutes: int = Field(default=30)

    internal_api_key: str = Field(default="internal-dev-key")
    appointment_service_url: str = Field(default="http://localhost:8001")
    notification_service_url: str = Field(default="http://localhost:8002")
    
    #  CORS (React dev server) 
    cors_origins: List[str] = Field(default=["http://localhost:3000"])

    model_config = SettingsConfigDict(
        env_file="user_service/.env",
        env_file_encoding="utf-8",
        case_sensitive=False,
        extra="ignore",
    )


@lru_cache
def get_settings() -> Settings:
    """
    Cached settings accessor. A plain function call — pydantic-settings reads the .env file once 
    and this keeps every caller on the same instance.
    """
    return Settings()
