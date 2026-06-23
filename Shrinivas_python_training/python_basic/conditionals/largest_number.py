"""
9. Find the largest of three numbers.
"""


def find_largest(num1: int, num2: int, num3: int) -> int:
    """
    This function finds and returns the largest of three numbers.
    """

    if num1 >= num2 and num1 >= num3:
        return num1
    elif num2 >= num1 and num2 >= num3:
        return num2
    else:
        return num3


# MAIN PROGRAM

a = int(input("Enter first number: "))
b = int(input("Enter second number: "))
c = int(input("Enter third number: "))

largest = find_largest(a, b, c)

print("Largest number is:", largest)