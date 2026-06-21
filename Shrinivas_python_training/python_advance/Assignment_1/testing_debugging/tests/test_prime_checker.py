# Pytest test cases for prime number checker.

import sys
import os

sys.path.append(
    os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
)

from prime_checker import is_prime


def test_prime_number():
    assert is_prime(7) is True


def test_non_prime_number():
    assert is_prime(8) is False


def test_zero():
    assert is_prime(0) is False


def test_one():
    assert is_prime(1) is False


def test_negative_number():
    assert is_prime(-5) is False