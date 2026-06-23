"""
20. Use reduce() to find the product of all elements in a list.
"""

from functools import reduce


def main():
    """
    Main function to calculate product of list elements using reduce.
    """
    numbers = [1, 2, 3, 4, 5]

    product = reduce(lambda a, b: a * b, numbers)

    print("Product:", product)


if __name__ == "__main__":
    main()