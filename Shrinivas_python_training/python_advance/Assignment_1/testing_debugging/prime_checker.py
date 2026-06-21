# Function to check whether a number is prime.

def is_prime(number: int) -> bool:
    """
    Return True if the number is prime, otherwise return False.
    """
    if number <= 1:
        return False

    for divisor in range(2, number):
        if number % divisor == 0:
            return False

    return True