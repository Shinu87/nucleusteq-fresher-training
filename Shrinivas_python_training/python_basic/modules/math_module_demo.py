"""
22. Use math module to find square root, power, and factorial.
"""

import math


def find_square_root(number: float) -> float:
    """
    Returns the square root of a number using math.sqrt().

    Time Complexity: O(1)
    Space Complexity: O(1)
    """

    return math.sqrt(number)


def find_power(base: float, exponent: float) -> float:
    """
    Returns base raised to the given exponent using math.pow().

    Time Complexity: O(1)
    Space Complexity: O(1)
    """

    return math.pow(base, exponent)


def find_factorial(number: int) -> int:
    """
    Returns factorial of a number using math.factorial().

    Time Complexity: O(n)
    Space Complexity: O(1)
    """

    return math.factorial(number)

# MAIN PROGRAM

number = float(input("Enter a number for square root: "))

if number < 0:
    print("Square root is not defined for negative numbers.")
else:
    print("Square Root:", find_square_root(number))

base = float(input("\nEnter base: "))
exponent = float(input("Enter exponent: "))
print("Power:", find_power(base, exponent))

factorial_number = int(input("\nEnter a number for factorial: "))

if factorial_number < 0:
    print("Factorial is not defined for negative numbers.")
else:
    print("Factorial:", find_factorial(factorial_number))