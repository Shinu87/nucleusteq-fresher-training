"""
25. Create a list of 10 numbers and find sum, max, sort it, and remove duplicates.
This program shows common list operations.
"""

def calculate_sum(numbers: list[int]) -> int:
    """
    Returns the sum of all elements in the list.

    Time Complexity: O(n)
    Space Complexity: O(1)
    """

    return sum(numbers)


def find_maximum(numbers: list[int]) -> int:
    """
    Returns the maximum element from the list.

    Time Complexity: O(n)
    Space Complexity: O(1)
    """

    return max(numbers)


def sort_numbers(numbers: list[int]) -> list[int]:
    """
    Returns a sorted version of the given list.

    Time Complexity: O(n log n)
    Space Complexity: O(n)
    """

    return sorted(numbers)


def remove_duplicates(numbers: list[int]) -> list[int]:
    """
    Removes duplicate elements from a list while keeping the original order.

    Approaches:

    1. Using dict.fromkeys()
    - Convert the list into a dictionary and then back to a list.
    - Dictionaries keep insertion order so the original order is maintained.
    - Time Complexity: O(n)
    - Space Complexity: O(n)

    2. Using a Separate List
    - Check each element and add it only if it is not already present.
    - Time Complexity: O(n²)
    - Space Complexity: O(n)

    3. Using set()
    - Convert the list to a set to remove duplicates.
    - Fast and simple but the original order may not be preserved.
    - Time Complexity: O(n)
    - Space Complexity: O(n)

    The dict.fromkeys() approach is used here because it removes duplicates while preserving the order of elements.
    """

    return list(dict.fromkeys(numbers))

# MAIN PROGRAM

numbers = list(map(int,input("Enter 10 numbers separated by spaces: ").split()))

if len(numbers) != 10:
    print("Please enter exactly 10 numbers.")
else:
    print("\nOriginal List:", numbers)

    print("Sum:", calculate_sum(numbers))

    print("Maximum Number:", find_maximum(numbers))

    print("Sorted List:", sort_numbers(numbers))

    print("List Without Duplicates:", remove_duplicates(numbers))