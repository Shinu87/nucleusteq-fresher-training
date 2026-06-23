"""
2. Write a program to divide two numbers entered by the user and handle ZeroDivisionError.
"""

def divide_numbers() -> None:
    """
    Takes two integer inputs from the user and performs division.
    Handles ValueError and ZeroDivisionError.
    """
    try:
        first_number = int(input("Enter the first number: "))
        second_number = int(input("Enter the second number: "))

        result = first_number / second_number
        print(f"Result: {result}")

    except ValueError:
        print("Please enter valid integer values.")

    except ZeroDivisionError:
        print("Division by zero is not allowed.")


divide_numbers()