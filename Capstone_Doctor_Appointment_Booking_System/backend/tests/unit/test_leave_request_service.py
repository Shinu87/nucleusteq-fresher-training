from datetime import date
from types import SimpleNamespace
from unittest.mock import AsyncMock

import pytest
from beanie import PydanticObjectId
from pydantic import ValidationError

from backend.constants.leave_request_status import LeaveRequestStatus
from backend.exceptions.custom_exceptions import (
    DoctorProfileSyncMissingException,
)
from backend.schemas.request.leave_request_request import LeaveRequestCreate
from backend.services.leave_request_service import LeaveRequestService


# Fixtures

@pytest.fixture
def leave_request_repository():
    return AsyncMock()


@pytest.fixture
def availability_repository():
    return AsyncMock()


@pytest.fixture
def appointment_repository():
    return AsyncMock()


@pytest.fixture
def doctor_repository():
    return AsyncMock()


@pytest.fixture
def user_repository():
    return AsyncMock()


@pytest.fixture
def notification_service():
    return AsyncMock()


@pytest.fixture
def leave_request_service(
    leave_request_repository,
    availability_repository,
    appointment_repository,
    doctor_repository,
    user_repository,
    notification_service,
):
    return LeaveRequestService(
        leave_request_repository=leave_request_repository,
        availability_repository=availability_repository,
        appointment_repository=appointment_repository,
        doctor_repository=doctor_repository,
        user_repository=user_repository,
        notification_service=notification_service,
    )


# Helpers

def _make_payload():
    return LeaveRequestCreate(
        date=date(2026, 8, 10),
        start_time="09:00",
        end_time="12:00",
        reason="Medical Emergency",
    )


def _make_leave_request(**overrides):
    leave = SimpleNamespace(
        id=PydanticObjectId(),
        doctor_id=PydanticObjectId(),
        date=date(2026, 8, 10),
        start_time="09:00",
        end_time="12:00",
        reason="Medical Emergency",
        request_status=LeaveRequestStatus.PENDING,
    )

    for key, value in overrides.items():
        setattr(leave, key, value)

    return leave


# request_leave()

async def test_request_leave_success(
    mocker,
    leave_request_service,
    doctor_repository,
    leave_request_repository,
):
    doctor_id = PydanticObjectId()

    doctor_repository.get_by_id.return_value = AsyncMock()

    fake_leave = _make_leave_request(
        doctor_id=doctor_id,
    )

    mocker.patch(
        "backend.services.leave_request_service.LeaveRequest",
        return_value=fake_leave,
    )

    leave_request_repository.insert.side_effect = lambda x: x

    result = await leave_request_service.request_leave(
        doctor_id,
        _make_payload(),
    )

    assert result == fake_leave

    assert result.doctor_id == doctor_id

    assert result.request_status == LeaveRequestStatus.PENDING

    leave_request_repository.insert.assert_awaited_once_with(
        fake_leave,
    )


async def test_request_leave_raises_when_doctor_missing(
    leave_request_service,
    doctor_repository,
):
    doctor_repository.get_by_id.return_value = None

    with pytest.raises(
        DoctorProfileSyncMissingException,
    ):
        await leave_request_service.request_leave(
            PydanticObjectId(),
            _make_payload(),
        )


def test_leave_request_schema_rejects_invalid_time_range():
    with pytest.raises(ValidationError):
        LeaveRequestCreate(
            date=date(2026, 8, 10),
            start_time="12:00",
            end_time="11:00",
            reason="Emergency",
        )


# list_my_requests()

async def test_list_my_requests(
    leave_request_service,
    leave_request_repository,
):
    doctor_id = PydanticObjectId()

    requests = [
        _make_leave_request(),
        _make_leave_request(
            id=PydanticObjectId(),
        ),
    ]

    leave_request_repository.find_by_doctor.return_value = requests

    result = await leave_request_service.list_my_requests(
        doctor_id,
    )

    assert result == requests

    leave_request_repository.find_by_doctor.assert_awaited_once_with(
        doctor_id,
    )


# list_requests()

async def test_list_requests_with_filter(
    leave_request_service,
    leave_request_repository,
):
    requests = [
        _make_leave_request(),
    ]

    leave_request_repository.find_by_status.return_value = requests

    result = await leave_request_service.list_requests(
        LeaveRequestStatus.PENDING,
    )

    assert result == requests

    leave_request_repository.find_by_status.assert_awaited_once_with(
        LeaveRequestStatus.PENDING,
    )


async def test_list_requests_without_filter(
    leave_request_service,
    leave_request_repository,
):
    requests = [
        _make_leave_request(),
        _make_leave_request(
            id=PydanticObjectId(),
        ),
    ]

    leave_request_repository.find_by_status.return_value = requests

    result = await leave_request_service.list_requests(
        None,
    )

    assert len(result) == 2

    leave_request_repository.find_by_status.assert_awaited_once_with(
        None,
    )