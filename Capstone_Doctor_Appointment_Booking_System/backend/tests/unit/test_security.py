from backend.utils.security import hash_password, verify_password


def test_hash_password_returns_a_different_string_than_the_plain_password():
    plain_password = "Secret@123"

    hashed = hash_password(plain_password)

    assert hashed != plain_password
    assert isinstance(hashed, str)
    assert len(hashed) > 0


def test_hash_password_is_not_deterministic():
    plain_password = "Secret@123"

    first_hash = hash_password(plain_password)
    second_hash = hash_password(plain_password)

    assert first_hash != second_hash


def test_verify_password_succeeds_for_the_correct_password():
    plain_password = "Secret@123"
    hashed = hash_password(plain_password)

    assert verify_password(plain_password, hashed) is True


def test_verify_password_fails_for_the_wrong_password():
    hashed = hash_password("Secret@123")

    assert verify_password("WrongPassword@1", hashed) is False


def test_verify_password_fails_for_empty_password():
    hashed = hash_password("Secret@123")

    assert verify_password("", hashed) is False
