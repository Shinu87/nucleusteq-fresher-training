"""
28. Create a tuple and access elements.
"""


def display_tuple_elements(student_details: tuple) -> None:
    """
    Displays tuple elements using different access methods.

    Time Complexity: O(1)
    Space Complexity: O(1)
    """

    print("First Element:", student_details[0])

    print("Last Element:", student_details[-1])

    print("Tuple Slice:", student_details[1:4])


# MAIN PROGRAM

student_details = (
    "Shrinivas",
    422219,
    "CSE",
    "NIT Andhra Pradesh",
    "Python"
)

print("Complete Tuple:", student_details)

display_tuple_elements(student_details)