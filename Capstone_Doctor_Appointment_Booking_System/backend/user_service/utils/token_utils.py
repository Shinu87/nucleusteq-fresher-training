"""
Helpers for the one-time "set your password" token a doctor gets emailed
after an admin approves them.

We never store the raw token in the database - only its SHA-256 hash.
That way, even if someone got read access to the database, they couldn't
use what they see there to set a doctor's password themselves.
"""

import hashlib
import secrets
from datetime import datetime, timedelta, timezone

SETUP_TOKEN_VALID_HOURS = 24


def generate_setup_token() -> str:
    """Generates a long, random, URL-safe token to put in the email link."""
    return secrets.token_urlsafe(32)


def hash_setup_token(raw_token: str) -> str:
    """Turns the raw token into the value we actually store in MongoDB."""
    return hashlib.sha256(raw_token.encode("utf-8")).hexdigest()


def get_setup_token_expiry() -> datetime:
    """Setup links expire after SETUP_TOKEN_VALID_HOURS hours."""
    return datetime.now(timezone.utc) + timedelta(hours=SETUP_TOKEN_VALID_HOURS)


def is_setup_token_expired(expiry: datetime) -> bool:
    if expiry.tzinfo is None:
        expiry = expiry.replace(tzinfo=timezone.utc)
    return datetime.now(timezone.utc) > expiry