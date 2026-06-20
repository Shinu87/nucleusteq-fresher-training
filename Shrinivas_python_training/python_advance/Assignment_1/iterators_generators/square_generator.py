"""
11. Write a generator function that yields square numbers up to N.
"""

def generate_squares(limit: int):
    """
    Generator function to generate square numbers up to N.
    """
    for number in range(1, limit + 1):
        yield number * number

gen = generate_squares(10)

for _ in range(5):
    print(next(gen))

for _ in range(5):
    print(next(gen))  # continues, not restart