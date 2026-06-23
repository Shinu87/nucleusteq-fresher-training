# Utility functions module.

def reverse_text(text: str) -> str:
    """
    Reverse a string and return the result.
    """
    return text[::-1]


def count_vowels(text: str) -> int:
    """
    Find the number of vowels in a string.
    """
    vowels = "aeiouAEIOU"

    return sum(1 for character in text if character in vowels)