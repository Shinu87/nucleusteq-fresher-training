"""
32. Create a student dictionary and access values.

Ways to Access Dictionary Values:

1. Using Square Brackets
   - Access values using dictionary[key].
   - Works when the key is present in the dictionary.

2. Using get() Method
   - Access values using dictionary.get(key).
   - Returns None if the key is not found instead of raising an error.

The get() method is often preferred when we are not sure whether a key exists in the dictionary.
"""

def display_student_details(student: dict[str, str | int | float]) -> None:
    """
    Displays student details.

    Time Complexity: O(1)
    Space Complexity: O(1)
    """

    print("Student Name:", student["name"])

    print("Roll Number:", student["roll_number"])

    print("Branch:", student.get("branch"))

    print("CGPA:", student.get("cgpa"))


# MAIN PROGRAM 

student = {
    "name": "Shrinivas",
    "roll_number": 101,
    "branch": "CSE",
    "cgpa": 8.75,
    "college": "NIT Andhra Pradesh"
}

print("Student Dictionary:", student)

print("\nAccessing Values:")

display_student_details(student)