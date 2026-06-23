"""
6. Create a function that raises a ValueError if a number is negative.
"""

def validate_number(number: int) -> None:
    """
    Raises a ValueError if the number is negative.
    """
    if number < 0:
        raise ValueError("Negative numbers are not allowed.")

    print(f"Valid number: {number}")


user_number = int(input("Enter a number: "))
validate_number(user_number)