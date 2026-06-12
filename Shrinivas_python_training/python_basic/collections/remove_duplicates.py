"""
31. Remove duplicates from a list using set.

- Convert the list into a set.
- A set stores only unique values so duplicate elements are removed automatically.
- Convert the set back into a list.

- The original order of elements may not be maintained.

Alternative Approach:
- Use list(dict.fromkeys(numbers)) to remove duplicates while keeping the original order.
"""


def remove_duplicates(numbers: list[int]) -> list[int]:
    """
    Removes duplicate elements using a set.

    Time Complexity: O(n)
    Space Complexity: O(n)
    """

    return list(set(numbers))


# MAIN PROGRAM 

numbers = list(
    map(
        int,
        input("Enter numbers separated by spaces: ").split()
    )
)

print("\nOriginal List:", numbers)

print("List Without Duplicates:", remove_duplicates(numbers))