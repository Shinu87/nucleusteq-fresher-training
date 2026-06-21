"""
1. Write a program that takes a number as input and handles ValueError if the input is not a valid integer.
"""

def get_integer_input() -> int:
    """
    Takes an input from the user and returns it as an integer.
    Handles ValueError if the user enters a non-integer value.
    """
    try:
        user_number = int(input("Enter an integer: "))
        return user_number
    except ValueError:
        print("Invalid input. Please enter a valid integer.")


get_integer_input()