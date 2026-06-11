"""
10. Calculate grade based on marks (A/B/C/Fail).
"""


def calculate_grade(marks: int) -> str:
    """
    This function calculates grade based on marks.

    Grading rules:
    - 90 and above : A
    - 75 to 89     : B
    - 50 to 74     : C
    - below 50     : Fail
    """

    if marks >= 90:
        return "A"
    elif marks >= 75:
        return "B"
    elif marks >= 50:
        return "C"
    else:
        return "Fail"


# MAIN PROGRAM

marks = int(input("Enter marks (0-100): "))

grade = calculate_grade(marks)

print("Grade:", grade)