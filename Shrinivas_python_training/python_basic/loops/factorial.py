"""
14. Find factorial of a number.

This program shows two approaches:
1. Iterative (using loop)
2. Recursive (using function recursion)
"""


def factorial_loop(num: int) -> int:
    """
    Finds factorial using iterative approach (loop).

    Time Complexity: O(n)
    Space Complexity: O(1)
    """

    fact = 1

    for i in range(1, num + 1):
        fact *= i

    return fact


def factorial_recursive(num: int) -> int:
    """
    Finds factorial using recursion.

    Time Complexity: O(n)
    Space Complexity: O(n) - due to recursion stack
    """

    if num == 0 or num == 1:
        return 1

    return num * factorial_recursive(num - 1)


# MAIN PROGRAM

number = int(input("Enter a number: "))

if number < 0:
    print("Factorial is not defined for negative numbers")
else:
    print("\nUsing Loop Approach:")
    print("Factorial:", factorial_loop(number))

    print("\nUsing Recursion Approach:")
    print("Factorial:", factorial_recursive(number))