"""
30. Perform union, intersection, and difference on two sets.

Set Operations:

1. Union (|) => Combines all unique elements from both sets.

2. Intersection (&) => Returns common elements present in both sets.

3. Difference (-) => Returns elements present in the first set but not in the second.

Alternative Methods:
- set1.union(set2)
- set1.intersection(set2)
- set1.difference(set2)

"""


def perform_union(set1: set[int], set2: set[int]) -> set[int]:
    """
    Returns the union of two sets.

    Time Complexity: O(len(set1) + len(set2))
    Space Complexity: O(len(set1) + len(set2))
    """

    return set1 | set2


def perform_intersection(set1: set[int], set2: set[int]) -> set[int]:
    """
    Returns the intersection of two sets.

    Time Complexity: O(min(len(set1), len(set2)))
    Space Complexity: O(min(len(set1), len(set2)))
    """

    return set1 & set2


def perform_difference(set1: set[int], set2: set[int]) -> set[int]:
    """
    Returns elements present in set1 but not in set2.

    Time Complexity: O(len(set1))
    Space Complexity: O(len(set1))
    """

    return set1 - set2


# MAIN PROGRAM

set1 = set(
    map(
        int,
        input("Enter elements of Set 1 separated by spaces: ").split()
    )
)

set2 = set(
    map(
        int,
        input("Enter elements of Set 2 separated by spaces: ").split()
    )
)

print("\nSet 1:", set1)
print("Set 2:", set2)

print("\nUnion:", perform_union(set1, set2))

print("Intersection:", perform_intersection(set1, set2))

print("Difference (Set1 - Set2):", perform_difference(set1, set2))