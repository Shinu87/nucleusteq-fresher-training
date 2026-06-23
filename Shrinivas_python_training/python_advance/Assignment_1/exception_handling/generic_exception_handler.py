"""
5. Write a program that catches all exceptions and prints the error message.
"""

def get_list_element() -> None:
    """
    Retrieves an element from a list using a user provided index.
    Catches all exceptions and prints the error message.
    """
    try:
        numbers = [10, 20, 30, 40, 50]

        index = int(input("Enter an index: "))
        print(f"Element: {numbers[index]}")

    except Exception as error:
        print(f"An error occurred: {error}")


get_list_element()