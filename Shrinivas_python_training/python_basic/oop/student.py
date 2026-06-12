"""
40. Create a Student class with attributes and display details.
"""


class Student:
    """
    Represents a student with basic academic details.
    """

    def __init__(self, name: str, roll_number: int, branch: str, cgpa: float) -> None:
        """
        Constructor to initialize student attributes.
        """

        self.name = name
        self.roll_number = roll_number
        self.branch = branch
        self.cgpa = cgpa

    def display_details(self) -> None:
        """
        Displays student details.
        """

        print("Student Details")
        print("Name:", self.name)
        print("Roll Number:", self.roll_number)
        print("Branch:", self.branch)
        print("CGPA:", self.cgpa)


# MAIN PROGRAM

name = input("Enter name: ")
roll_number = int(input("Enter roll number: "))
branch = input("Enter branch: ")
cgpa = float(input("Enter CGPA: "))

student = Student(name, roll_number, branch, cgpa)

print()
student.display_details()