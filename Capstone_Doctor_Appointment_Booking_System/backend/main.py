"""
Single FastAPI application entrypoint for the backend.
"""

import logging
from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from backend.config import get_settings
from backend.database.connection import close_mongo_connection, connect_to_mongo
from backend.routers.admin_router import router as admin_router
from backend.routers.appointment_router import router as appointment_router
from backend.routers.auth_router import router as auth_router
from backend.routers.availability_router import router as availability_router
from backend.routers.doctor_router import router as doctor_router

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s | %(levelname)s | %(name)s | %(message)s",
)
logger = logging.getLogger(__name__)

settings = get_settings()


@asynccontextmanager
async def lifespan(app: FastAPI):
    await connect_to_mongo()
    logger.info("%s started in '%s' mode", settings.app_name, settings.app_env)
    yield
    await close_mongo_connection()
    logger.info("%s shut down", settings.app_name)


def create_app() -> FastAPI:
    app = FastAPI(
        title=settings.app_name,
        description="Doctor Appointment Booking System - Monolithic Backend",
        lifespan=lifespan,
    )

    app.add_middleware(
        CORSMiddleware,
        allow_origins=settings.cors_origins,
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    # All routes live under /api/v1 - same URLs as before
    app.include_router(auth_router, prefix=settings.api_v1_prefix)
    app.include_router(admin_router, prefix=settings.api_v1_prefix)
    app.include_router(availability_router, prefix=settings.api_v1_prefix)
    app.include_router(doctor_router, prefix=settings.api_v1_prefix)
    app.include_router(appointment_router, prefix=settings.api_v1_prefix)

    @app.get("/health", tags=["Health"])
    async def health_check():
        return {"status": "ok", "service": settings.app_name}

    return app


app = create_app()
