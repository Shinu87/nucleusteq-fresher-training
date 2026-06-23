"""
17. Write a function to calculate square of a number.
"""


def calculate_square_multiplication(num: int) -> int:
    """
    Calculates the square of a number using multiplication.

    Time Complexity: O(1)
    Space Complexity: O(1)
    """

    return num * num


def calculate_square_exponent(num: int) -> int:
    """
    Calculates the square of a number using exponent operator.

    Time Complexity: O(1)
    Space Complexity: O(1)
    """

    return num ** 2


# MAIN PROGRAM 

number = int(input("Enter a number: "))

print("\nUsing Multiplication:")
print("Square:", calculate_square_multiplication(number))

print("\nUsing Exponent Operator:")
print("Square:", calculate_square_exponent(number))