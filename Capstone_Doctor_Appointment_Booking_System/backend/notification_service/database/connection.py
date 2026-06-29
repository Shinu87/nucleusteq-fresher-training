"""
MongoDB connection management for Notification Service, via PyMongo's
native async client + Beanie.
"""

import logging

from beanie import init_beanie
from pymongo import AsyncMongoClient

from notification_service.config import get_settings
from notification_service.models.notification import Notification

logger = logging.getLogger(__name__)

settings = get_settings()

client: AsyncMongoClient | None = None


async def connect_to_mongo() -> None:
    """Open the MongoDB client and initialize Beanie with all Document models."""
    global client

    document_models: list = [
        Notification,
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