"""
18. Use map() to convert a list of numbers into their squares.
"""

def main():
    """
    Main function to run map operation for squaring numbers.
    """
    numbers = [1, 2, 3, 4, 5]

    squared_numbers = map(lambda x: x ** 2, numbers)

    print("Squared Numbers:")

    for number in squared_numbers:
        print(number)


if __name__ == "__main__":
    main()