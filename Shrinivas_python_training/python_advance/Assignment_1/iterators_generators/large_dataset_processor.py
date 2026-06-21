"""
15. Write a program that processes a large dataset using a generator instead of storing all values in a list.
- Generator helps to save memory
- Data is processed one by one
- Useful for large datasets
"""

def read_large_data(limit: int):
    """
    Simulate a large dataset and give values one by one using a generator.
    """
    for i in range(1, limit + 1):
        yield i


def process_data():
    """
    Process data from generator without storing full data in memory.
    """
    for value in read_large_data(1000000):
        result = value * 2

        # printing only first few values for sample output
        if value <= 5:
            print("Processed:", result)


process_data()