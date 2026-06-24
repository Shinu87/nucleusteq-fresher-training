import logging
from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from backend.config import get_settings
from backend.database.connection import close_mongo_connection, connect_to_mongo

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s | %(levelname)s | %(name)s | %(message)s",
)
logger = logging.getLogger(__name__)

settings = get_settings()


@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup
    await connect_to_mongo()
    logger.info("%s starting up in '%s' mode", settings.app_name, settings.app_env)
    yield
    # Shutdown
    await close_mongo_connection()
    logger.info("%s shutting down", settings.app_name)


def create_app() -> FastAPI:
    app = FastAPI(
        title=settings.app_name,
        version="0.1.0",
        lifespan=lifespan,
    )

    app.add_middleware(
        CORSMiddleware,
        allow_origins=settings.cors_origins,
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    @app.get("/", tags=["Health"])
    async def root():
        return {
            "service": settings.app_name,
            "status": "ok",
            "env": settings.app_env,
        }

    @app.get("/health", tags=["Health"])
    async def health_check():
        return {"message": "Doctor Appointment Booking System API Running"}

    return app


app = create_app()
