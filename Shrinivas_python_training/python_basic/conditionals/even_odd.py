"""
7. Write a program to check whether a number is even or odd.
"""

def check_even_odd(num: int) -> None:
    """
    This function checks whether a given number is even or odd.
    It has two different approaches:
    1. Using modulus operator (%)
    2. Using bitwise AND operator (&)
    """

    # Method 1: Using modulus operator
    if num % 2 == 0:
        print("Method 1 (mod %): Even")
    else:
        print("Method 1 (mod %): Odd")

    # Method 2: Using bitwise operator
    # (num & 1) == 1 means odd number
    if num & 1:
        print("Method 2 (bitwise &): Odd")
    else:
        print("Method 2 (bitwise &): Even")

# MAIN PROGRAM

number = int(input("Enter a number: "))
check_even_odd(number)