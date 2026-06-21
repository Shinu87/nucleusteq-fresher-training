"""
34. Create a function with a logical bug and use pdb to identify the issue.
"""

import pdb


def calculate_average_rating(ratings: list[int]) -> float:
    """
    Find the average rating of employees.
    """
    total_rating = sum(ratings)

    pdb.set_trace()  # Debugger starts here

    average_rating = total_rating / 5  # Logical bug

    return average_rating


def main():
    """
    Main function to execute the program.
    """
    employee_ratings = [4, 5, 3]

    average = calculate_average_rating(employee_ratings)

    print(f"Average Rating: {average}")


if __name__ == "__main__":
    main()