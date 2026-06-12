"""
19. Write a function that returns maximum number from a list.
"""


def find_maximum_manual(numbers: list[int]) -> int:
    """
    Returns the maximum number from a list using manual comparison.

    Time Complexity: O(n)
    Space Complexity: O(1)
    """

    maximum = numbers[0]

    for number in numbers:
        if number > maximum:
            maximum = number

    return maximum


def find_maximum_builtin(numbers: list[int]) -> int:
    """
    Returns the maximum number from a list using Python built-in max().

    Time Complexity: O(n)
    Space Complexity: O(1)
    """

    return max(numbers)


# MAIN PROGRAM

numbers = list(map(int, input("Enter numbers separated by spaces: ").split()))

if not numbers:
    print("List cannot be empty.")
else:
    print("\nUsing Manual Approach:")
    print("Maximum Number:", find_maximum_manual(numbers))

    print("\nUsing Built-in Function:")
    print("Maximum Number:", find_maximum_builtin(numbers))