"""
4. Handle multiple exceptions in a single program.
"""

def calculate_total_marks() -> None:
    """
    Calculates the total marks of a student by adding bonus marks.
    Handles KeyError and ValueError exceptions.
    """
    try:
        student_marks = {
            "Mohan": 85,
            "Shinu": 92
        }

        student_name = input("Enter student name: ").title()
        bonus_marks = int(input("Enter bonus marks: "))

        total_marks = student_marks[student_name] + bonus_marks
        print(f"Total Marks: {total_marks}")

    except KeyError:
        print("Student not found.")

    except ValueError:
        print("Bonus marks must be an integer.")


calculate_total_marks()