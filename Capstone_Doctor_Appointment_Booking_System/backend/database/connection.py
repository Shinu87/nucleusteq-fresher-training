"""
MongoDB connection management via Motor + Beanie.

connect_to_mongo() is called once on FastAPI startup (main.py's lifespan handler) and 
close_mongo_connection() on shutdown.
"""

import logging

from beanie import init_beanie
from motor.motor_asyncio import AsyncIOMotorClient

from backend.config import get_settings

logger = logging.getLogger(__name__)

settings = get_settings()

client: AsyncIOMotorClient | None = None


async def connect_to_mongo() -> None:
    """Open the Motor client and initialize Beanie with all Document models."""
    global client

    document_models: list = [
    ]

    client = AsyncIOMotorClient(settings.mongo_uri)
    database = client[settings.mongo_db_name]

    await init_beanie(database=database, document_models=document_models)
    logger.info("Connected to MongoDB database '%s'", settings.mongo_db_name)


async def close_mongo_connection() -> None:
    """Close the Motor client cleanly on application shutdown."""
    global client
    if client is not None:
        client.close()
        logger.info("MongoDB connection closed")
