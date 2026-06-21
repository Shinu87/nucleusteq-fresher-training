"""
40. Create a package for mathematical operations (add, subtract, multiply, divide) and use it.
"""

from math_operations import (
    add,
    subtract,
    multiply,
    divide
)


def main():
    """
    Main function to show mathematical operations.
    """
    first_number = 20
    second_number = 5

    print(f"Addition: {add(first_number, second_number)}")
    print(f"Subtraction: {subtract(first_number, second_number)}")
    print(f"Multiplication: {multiply(first_number, second_number)}")
    print(f"Division: {divide(first_number, second_number)}")


if __name__ == "__main__":
    main()