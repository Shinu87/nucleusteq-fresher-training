"""
23. Generate random numbers using random module.
"""

import random


def generate_random_integer(start: int, end: int) -> int:
    """
    Generates a random integer between start and end.

    Time Complexity: O(1)
    Space Complexity: O(1)
    """

    return random.randint(start, end)


def generate_random_float() -> float:
    """
    Generates a random floating-point number between 0 and 1.

    Time Complexity: O(1)
    Space Complexity: O(1)
    """

    return random.random()


def generate_random_number_from_list(numbers: list[int]) -> int:
    """
    Returns a random element from a list.

    Time Complexity: O(1)
    Space Complexity: O(1)
    """

    return random.choice(numbers)


# MAIN PROGRAM

print("Random Integer:", generate_random_integer(1, 100))

print("Random Float:", generate_random_float())

sample_numbers = [10, 20, 30, 40, 50]

print("Random Choice:", generate_random_number_from_list(sample_numbers))