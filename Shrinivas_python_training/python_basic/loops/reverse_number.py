"""
15. Reverse a number using loop.
"""

def reverse_number_loop(num: int) -> int:
    """
    This function reverses a number using iterative (loop) approach.

    Time Complexity: O(d) where d = number of digits
    Space Complexity: O(1)
    """

    reversed_number = 0

    while num > 0:
        digit = num % 10
        reversed_number = reversed_number * 10 + digit
        num = num // 10

    return reversed_number


# MAIN PROGRAM

number = int(input("Enter a number: "))

if number < 0:
    print("Please enter a non-negative number")
else:
    result = reverse_number_loop(number)
    print("Reversed number:", result)