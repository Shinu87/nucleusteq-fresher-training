"""
Notification Service entrypoint.
"""

import logging
from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from notification_service.config import get_settings
from notification_service.database.connection import close_mongo_connection, connect_to_mongo
from notification_service.routers.internal_router import router as internal_router

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

settings = get_settings()


@asynccontextmanager
async def lifespan(app: FastAPI):
    await connect_to_mongo()
    logger.info("%s started", settings.app_name)
    yield
    await close_mongo_connection()
    logger.info("%s shut down", settings.app_name)


def create_app() -> FastAPI:
    app = FastAPI(title=settings.app_name, lifespan=lifespan)

    app.add_middleware(
        CORSMiddleware,
        allow_origins=settings.cors_origins,
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    app.include_router(internal_router)

    @app.get("/health", tags=["Health"])
    async def health_check():
        return {"status": "ok", "service": settings.app_name}

    return app


app = create_app()