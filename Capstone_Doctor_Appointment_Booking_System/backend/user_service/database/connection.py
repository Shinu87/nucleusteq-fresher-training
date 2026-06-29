"""
MongoDB connection management via PyMongo's native async client + Beanie.

I used to use Motor here, but Beanie 2.x calls a method
(append_metadata) on the underlying client that Motor's wrapper doesn't
expose properly - it ends up crashing on startup with
"MotorDatabase object is not callable". 
Beanie 2.x actually doesn't need
Motor at all anymore, it works directly with pymongo's own async client,
so that's what I use instead.

connect_to_mongo() is called once on FastAPI startup (main.py's lifespan handler) and 
close_mongo_connection() on shutdown.
"""

import logging

from beanie import init_beanie
from pymongo import AsyncMongoClient

from user_service.config import get_settings
from user_service.models.doctor_profile import DoctorProfile
from user_service.models.user import User


logger = logging.getLogger(__name__)

settings = get_settings()

client: AsyncMongoClient | None = None


async def connect_to_mongo() -> None:
    """Open the MongoDB client and initialize Beanie with all Document models."""
    global client

    document_models: list = [
        User,
        DoctorProfile,
    ]

    client = AsyncMongoClient(settings.mongo_uri)
    database = client[settings.mongo_db_name]

    await init_beanie(database=database, document_models=document_models)
    logger.info("Connected to MongoDB database '%s'", settings.mongo_db_name)


async def close_mongo_connection() -> None:
    """Close the MongoDB client cleanly on application shutdown."""
    global client
    if client is not None:
        await client.close()
        logger.info("MongoDB connection closed")