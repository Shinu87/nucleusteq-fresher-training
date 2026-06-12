"""
29. Convert tuple into list and modify it.

Steps:
1. Convert the tuple into a list using list().
2. Modify the required element in the list.
3. Use the updated list as needed.

Time Complexity:
- Converting tuple to list: O(n)
- Updating an element: O(1)

Space Complexity:
- O(n)
"""


def convert_tuple_to_list(data: tuple) -> list:
    """
    Converts a tuple into a list.
    """

    return list(data)


def modify_list(data: list, index: int, new_value) -> None:
    """
    Modifies a list element at the given index.
    """

    data[index] = new_value


# MAIN PROGRAM

student_details = (
    "Shrinivas",
    422219,
    "CSE",
    "Python"
)

print("Original Tuple:", student_details)

student_details_list = convert_tuple_to_list(student_details)

modify_list(student_details_list, 1, 9221615)

print("Modified List:", student_details_list)