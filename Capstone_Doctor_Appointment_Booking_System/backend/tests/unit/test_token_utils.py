from datetime import datetime, timedelta, timezone

from backend.constants.validation_constants import ValidationLimits
from backend.utils.token_utils import (
    generate_setup_token,
    get_setup_token_expiry,
    hash_setup_token,
    is_setup_token_expired,
)


def test_generate_setup_token_returns_a_non_empty_url_safe_string():
    token = generate_setup_token()

    assert isinstance(token, str)
    assert len(token) > 20


def test_generate_setup_token_returns_a_different_token_every_time():
    first_token = generate_setup_token()
    second_token = generate_setup_token()

    assert first_token != second_token


def test_hash_setup_token_is_deterministic():
    raw_token = "my-raw-setup-token"

    assert hash_setup_token(raw_token) == hash_setup_token(raw_token)


def test_hash_setup_token_differs_for_different_inputs():
    assert hash_setup_token("token-one") != hash_setup_token("token-two")


def test_get_setup_token_expiry_is_in_the_future_by_the_configured_number_of_hours(mocker):
    fixed_now = datetime(2026, 1, 1, tzinfo=timezone.utc)
    mock_datetime = mocker.patch("backend.utils.token_utils.datetime")
    mock_datetime.now.return_value = fixed_now

    expiry = get_setup_token_expiry()

    expected_expiry = fixed_now + timedelta(hours=ValidationLimits.SETUP_TOKEN_VALID_HOURS)
    assert expiry == expected_expiry


def test_is_setup_token_expired_returns_true_for_a_past_expiry():
    past_expiry = datetime.now(timezone.utc) - timedelta(hours=1)

    assert is_setup_token_expired(past_expiry) is True


def test_is_setup_token_expired_returns_false_for_a_future_expiry():
    future_expiry = datetime.now(timezone.utc) + timedelta(hours=1)

    assert is_setup_token_expired(future_expiry) is False


def test_is_setup_token_expired_handles_naive_datetimes():
    naive_future_expiry = datetime.utcnow() + timedelta(hours=1)

    assert is_setup_token_expired(naive_future_expiry) is False
