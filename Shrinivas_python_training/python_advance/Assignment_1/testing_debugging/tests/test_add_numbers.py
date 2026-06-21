"""
32. Write pytest test cases for a function that adds two numbers.
"""

# Pytest test cases for add function.

import sys
import os

sys.path.append(
    os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
)

from add_numbers import add


def test_add_positive_numbers():
    assert add(3, 2) == 5


def test_add_negative_numbers():
    assert add(-3, -2) == -5


def test_add_zero():
    assert add(5, 0) == 5