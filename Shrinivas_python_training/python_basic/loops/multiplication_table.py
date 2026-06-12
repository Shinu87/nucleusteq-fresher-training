"""
13. Print multiplication table of a number.
"""


def print_multiplication_table(num: int) -> None:
    """
    This function prints the multiplication table of a given number.
    """

    for i in range(1, 11):
        print(f"{num} x {i} = {num * i}")


# MAIN PROGRAM

number = int(input("Enter a number: "))
print_multiplication_table(number)