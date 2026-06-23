"""
8. Check whether a number is positive, negative, or zero.
"""


def check_number_sign(num: int) -> None:
    """
    This function checks whether a given number is:
    - Positive
    - Negative
    - Zero
    """

    if num > 0:
        print("Positive number")
    elif num < 0:
        print("Negative number")
    else:
        print("Zero")


# MAIN PROGRAM

number = int(input("Enter a number: "))
check_number_sign(number)