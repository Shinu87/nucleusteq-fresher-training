# Module for division operation.

def divide(a: int, b: int) -> float:
    """
    Division of two numbers.
    """
    if b == 0:
        raise ValueError("Division by zero is not allowed.")

    return a / b