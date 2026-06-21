# Program to create and use a custom AgeException.

from custom_exceptions import AgeException

def validate_age(age: int) -> None:
    """
    Validates the age and raises AgeException if age is less than 18.
    """
    if age < 18:
        raise AgeException("Age must be at least 18.")

    print(f"Valid age: {age}")


user_age = int(input("Enter your age: "))
validate_age(user_age)