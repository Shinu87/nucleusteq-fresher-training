"""
17. Write a lambda function to find the square of a number.
"""

def main():
    """
    Take input and find square using lambda
    """

    n = int(input("Enter a number: "))

    square = lambda x: x * x

    print(f"Square: {square(n)}")


if __name__ == "__main__":
    main()