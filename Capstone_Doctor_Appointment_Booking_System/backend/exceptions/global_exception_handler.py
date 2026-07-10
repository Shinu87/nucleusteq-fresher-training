"""
Central place where every exception type the application can raise gets
turned into a consistent JSON error response.
"""

import logging

from fastapi import FastAPI, Request, status
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from starlette.exceptions import HTTPException as StarletteHTTPException

from backend.constants.common_errors import INTERNAL_SERVER_ERROR, VALIDATION_ERROR
from backend.constants.common_messages import INTERNAL_SERVER_ERROR as INTERNAL_SERVER_ERROR_MESSAGE
from backend.constants.common_messages import VALIDATION_FAILED
from backend.exceptions.custom_exceptions import AppException

logger = logging.getLogger(__name__)


def _error_response(status_code: int, error_code: str, message: str, details=None, headers=None):
    content = {"success": False, "error_code": error_code, "message": message}
    if details is not None:
        content["details"] = details
    return JSONResponse(status_code=status_code, content=content, headers=headers)


def register_exception_handlers(app: FastAPI) -> None:

    @app.exception_handler(AppException)
    async def app_exception_handler(request: Request, exc: AppException) -> JSONResponse:
        logger.info(
            "%s | %s %s -> %s: %s",
            exc.__class__.__name__,
            request.method,
            request.url.path,
            exc.status_code,
            exc.message,
        )
        return _error_response(exc.status_code, exc.error_code, exc.message, headers=exc.headers)
    
    @app.exception_handler(RequestValidationError)
    async def validation_exception_handler(
        request: Request, exc: RequestValidationError
    ) -> JSONResponse:
        logger.info(
            "Validation error | %s %s -> %s",
            request.method,
            request.url.path,
            exc.errors(),
        )

        errors = []
        for error in exc.errors():
            error.pop("ctx", None) 
            errors.append(error)

        return _error_response(
            status.HTTP_422_UNPROCESSABLE_ENTITY,
            VALIDATION_ERROR,
            VALIDATION_FAILED,
            details=errors,
        )

    @app.exception_handler(StarletteHTTPException)
    async def http_exception_handler(
        request: Request, exc: StarletteHTTPException
    ) -> JSONResponse:
        logger.info(
            "HTTPException | %s %s -> %s: %s",
            request.method,
            request.url.path,
            exc.status_code,
            exc.detail,
        )
        return _error_response(
            exc.status_code,
            INTERNAL_SERVER_ERROR if exc.status_code >= 500 else VALIDATION_ERROR,
            str(exc.detail),
            headers=exc.headers,
        )

    @app.exception_handler(Exception)
    async def unhandled_exception_handler(request: Request, exc: Exception) -> JSONResponse:
        logger.exception(
            "Unhandled exception | %s %s", request.method, request.url.path
        )
        return _error_response(
            status.HTTP_500_INTERNAL_SERVER_ERROR,
            INTERNAL_SERVER_ERROR,
            INTERNAL_SERVER_ERROR_MESSAGE,
        )
