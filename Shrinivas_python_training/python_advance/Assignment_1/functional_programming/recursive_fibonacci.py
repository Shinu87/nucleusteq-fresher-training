"""
22. Write a recursive function to calculate Fibonacci.
"""

def fibonacci(n: int) -> int:
    """
    Recursively returns the nth Fibonacci number.
    """
    if n == 0:
        return 0
    if n == 1:
        return 1

    return fibonacci(n - 1) + fibonacci(n - 2)


def main():
    """
    Main function to execute Fibonacci program.
    """
    num = int(input("Enter number of terms: "))

    if num < 0:
        print("Fibonacci is not defined for negative numbers.")
        return

    print("Fibonacci Series:")

    for i in range(num):
        print(fibonacci(i))


if __name__ == "__main__":
    main()