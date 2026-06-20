"""
21. Write a recursive function to calculate factorial.
"""

def factorial(n: int) -> int:
    """
    Recursively calculate factorial of a number.
    """
    if n == 0 or n == 1:
        return 1

    return n * factorial(n - 1)


def main():
    """
    Main function to execute factorial program.
    """
    num = int(input("Enter a number: "))

    if num < 0:
        print("Factorial is not defined for negative numbers.")
        return

    result = factorial(num)

    print(f"Factorial of {num} is: {result}")


if __name__ == "__main__":
    main()