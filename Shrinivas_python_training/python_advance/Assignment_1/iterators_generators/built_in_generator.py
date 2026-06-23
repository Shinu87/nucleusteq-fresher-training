"""
16. Show an example of a built-in generator (like range) and iterate over it.
"""

def demonstrate_range_generator() -> None:
    """
    Show how range works like a generator.
    """
    numbers = range(1, 11)

    for num in numbers:
        print(num)


demonstrate_range_generator()