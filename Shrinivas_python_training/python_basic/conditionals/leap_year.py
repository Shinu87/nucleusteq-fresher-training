"""
11. Check whether a year is a leap year.
"""


def is_leap_year(year: int) -> bool:
    """
    This function checks whether a given year is a leap year or not.

    Leap year rules:
        1. It is divisible by 4 AND not divisible by 100
        OR
        2. It is divisible by 400
    """

    if (year % 4 == 0 and year % 100 != 0) or (year % 400 == 0):
        return True
    else:
        return False


# MAIN PROGRAM

year = int(input("Enter a year: "))

if is_leap_year(year):
    print("Leap Year")
else:
    print("Not a Leap Year")