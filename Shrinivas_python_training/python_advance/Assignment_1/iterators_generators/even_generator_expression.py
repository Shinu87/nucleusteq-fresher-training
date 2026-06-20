"""
13. Write a generator expression to generate even numbers from 1 to 50.
"""

even_numbers = (num for num in range(1, 51) if num % 2 == 0)

for number in even_numbers:
    print(number)