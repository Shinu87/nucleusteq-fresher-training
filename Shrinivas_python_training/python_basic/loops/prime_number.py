"""
16. Check whether a number is prime.
"""

def is_prime_loop(num: int) -> bool:
    """
    This function checks whether a number is prime using loop.

    Logic:
    A number is prime if it is greater than 1 and has no divisors other than 1 and itself.

    Time Complexity: O(sqrt(n))
    Space Complexity: O(1)
    """

    if num <= 1:
        return False

    # checking divisibility up to square root of num
    i = 2
    while i * i <= num:
        if num % i == 0:
            return False
        i += 1

    return True


# MAIN PROGRAM

number = int(input("Enter a number: "))

if is_prime_loop(number):
    print("Prime Number")
else:
    print("Not a Prime Number")