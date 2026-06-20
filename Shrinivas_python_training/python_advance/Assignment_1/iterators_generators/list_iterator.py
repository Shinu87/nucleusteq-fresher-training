"""
9. Create an iterator for a list and print elements using next().
"""

def print_list_elements() -> None:
    """
    Function to create an iterator for a list and print its elements using next().
    """
    numbers = [1, 2, 3, 4, 5]

    iterator = iter(numbers)

    for _ in range(len(numbers)):
        print(next(iterator))


print_list_elements()