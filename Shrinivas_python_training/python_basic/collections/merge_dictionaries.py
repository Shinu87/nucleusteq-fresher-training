"""
34. Merge two dictionaries.

Ways to merge dictionaries:

1. Using update() method
   - Adds all key-value pairs from the second dictionary into the first one.
   - It changes the original dictionary.

2. Using | operator
   - Combines both dictionaries and creates a new one.
   - Original dictionaries remain unchanged.

- If both dictionaries have the same key the value from the second dictionary will overwrite the first one.
"""


def merge_using_update(dict1: dict, dict2: dict) -> dict:
    """
    Merges two dictionaries using update() method.

    Time Complexity: O(m)
    Space Complexity: O(1)
    """

    dict1.update(dict2)
    return dict1


def merge_using_operator(dict1: dict, dict2: dict) -> dict:
    """
    Merges two dictionaries using | operator.

    Time Complexity: O(n + m)
    Space Complexity: O(n + m)
    """

    return dict1 | dict2



# MAIN PROGRAM

dict1 = {
    "name": "Shrinivas",
    "age": 22,
    "branch": "CSE"
}

dict2 = {
    "college": "NIT Andhra Pradesh",
    "age": 23 
}

print("Dictionary 1:", dict1)
print("Dictionary 2:", dict2)

print("\nUsing update():", merge_using_update(dict1.copy(), dict2))

print("Using | operator:", merge_using_operator(dict1, dict2))
