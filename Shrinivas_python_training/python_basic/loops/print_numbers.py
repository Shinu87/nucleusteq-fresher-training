"""
12. Print numbers from 1 to 100 using loop.
"""


def print_numbers(start: int, end: int) -> None:
    """
    This function prints numbers from start to end using a loop.
    """

    for i in range(start, end + 1):
        print(i)


# MAIN PROGRAM

print_numbers(1, 100)