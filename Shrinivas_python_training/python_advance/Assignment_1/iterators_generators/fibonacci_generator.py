"""
12. Write a generator to produce Fibonacci numbers.
"""

def generate_fib(limit: int):
    """
    Generate Fibonacci numbers up to given number of terms.
    """
    a, b = 0, 1

    for _ in range(limit):
        yield a
        a, b = b, a + b


for number in generate_fib(10):
    print(number)