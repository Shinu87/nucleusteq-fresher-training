from unittest.mock import AsyncMock

import pytest

from backend.constants.notification_constants import NotificationStatus, NotificationType
from backend.models.notification import Notification
from backend.schemas.request.internal_request import SendNotificationRequest
from backend.services.notification_service import NotificationService


@pytest.fixture
def notification_repository():
    repo = AsyncMock()
    repo.insert.side_effect = lambda notification: notification
    return repo


@pytest.fixture
def notification_service(notification_repository):
    return NotificationService(notification_repository=notification_repository)


async def test_send_notification_setup_password_success(mocker, notification_service, notification_repository):
    mocker.patch(
        "backend.services.notification_service.build_setup_password_email_html",
        return_value="<html>setup</html>",
    )
    mock_send_email = mocker.patch(
        "backend.services.notification_service.send_email", new_callable=AsyncMock
    )

    payload = SendNotificationRequest(
        recipient_email="doctor@example.com",
        type=NotificationType.SETUP_PASSWORD,
        payload={"setup_link": "https://app.example.com/set-password/abc123"},
    )

    result = await notification_service.send_notification(payload)

    assert isinstance(result, Notification)
    assert result.status == NotificationStatus.SENT
    mock_send_email.assert_awaited_once()
    notification_repository.insert.assert_awaited_once()


async def test_send_notification_setup_password_marks_failed_when_email_send_raises(
    mocker, notification_service
):
    mocker.patch(
        "backend.services.notification_service.build_setup_password_email_html",
        return_value="<html>setup</html>",
    )
    mocker.patch(
        "backend.services.notification_service.send_email",
        new_callable=AsyncMock,
        side_effect=Exception("SMTP is down"),
    )

    payload = SendNotificationRequest(
        recipient_email="doctor@example.com",
        type=NotificationType.SETUP_PASSWORD,
        payload={"setup_link": "https://app.example.com/set-password/abc123"},
    )

    result = await notification_service.send_notification(payload)

    assert result.status == NotificationStatus.FAILED


async def test_send_notification_appointment_cancellation_success(mocker, notification_service):
    mocker.patch(
        "backend.services.notification_service.build_appointment_cancellation_email_html",
        return_value="<html>cancelled</html>",
    )
    mock_send_email = mocker.patch(
        "backend.services.notification_service.send_email", new_callable=AsyncMock
    )

    payload = SendNotificationRequest(
        recipient_email="patient@example.com",
        type=NotificationType.APPOINTMENT_CANCELLATION,
        payload={
            "patient_name": "Atharv Gokhale",
            "doctor_name": "Dr. Kulkarni",
            "appointment_date": "2026-08-01",
            "start_time": "09:00",
            "reason": "Doctor unavailable",
        },
    )

    result = await notification_service.send_notification(payload)

    assert result.status == NotificationStatus.SENT
    mock_send_email.assert_awaited_once()


async def test_send_notification_appointment_cancellation_marks_failed_when_email_send_raises(mocker, notification_service):
    mocker.patch(
        "backend.services.notification_service.build_appointment_cancellation_email_html",
        return_value="<html>cancelled</html>",
    )
    mocker.patch(
        "backend.services.notification_service.send_email",
        new_callable=AsyncMock,
        side_effect=Exception("SMTP is down"),
    )

    payload = SendNotificationRequest(
        recipient_email="patient@example.com",
        type=NotificationType.APPOINTMENT_CANCELLATION,
        payload={},
    )

    result = await notification_service.send_notification(payload)

    assert result.status == NotificationStatus.FAILED

async def test_send_notification_appointment_confirmation_success(
    mocker, notification_service
):
    mocker.patch(
        "backend.services.notification_service.build_appointment_confirmation_email_html",
        return_value="<html>confirmed</html>",
    )

    mock_send_email = mocker.patch(
        "backend.services.notification_service.send_email",
        new_callable=AsyncMock,
    )

    payload = SendNotificationRequest(
        recipient_email="patient@example.com",
        type=NotificationType.APPOINTMENT_CONFIRMATION,
        payload={
            "patient_name": "Atharv Gokhale",
            "doctor_name": "Dr. Kulkarni",
            "appointment_date": "2026-08-01",
            "start_time": "09:00",
            "payment_status": "PAID",
            "consultation_fee": 500,
        },
    )

    result = await notification_service.send_notification(payload)

    assert result.status == NotificationStatus.SENT
    mock_send_email.assert_awaited_once()

async def test_send_notification_appointment_confirmation_marks_failed_when_email_send_raises(
    mocker, notification_service
):
    mocker.patch(
        "backend.services.notification_service.build_appointment_confirmation_email_html",
        return_value="<html>confirmed</html>",
    )

    mocker.patch(
        "backend.services.notification_service.send_email",
        new_callable=AsyncMock,
        side_effect=Exception("SMTP is down"),
    )

    payload = SendNotificationRequest(
        recipient_email="patient@example.com",
        type=NotificationType.APPOINTMENT_CONFIRMATION,
        payload={},
    )

    result = await notification_service.send_notification(payload)

    assert result.status == NotificationStatus.FAILED