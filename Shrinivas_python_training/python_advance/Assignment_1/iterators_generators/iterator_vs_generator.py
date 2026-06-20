"""
14. Explain the difference between iterator and generator with a small example.

1. Iterator:
- It is an object used to iterate through values.
- We use iter() and next() to get values one by one.

2. Generator:
- It is a simple way to create iterators using yield.
- It is easier to write and uses less memory.
- It generates values only when needed (lazy evaluation).
"""

# Iterator Example

numbers = [1, 2, 3]

iterator = iter(numbers)

print("Iterator Output:")
print(next(iterator))
print(next(iterator))
print(next(iterator))


# Generator Example

def generate_numbers():
    yield 1
    yield 2
    yield 3


print("\nGenerator Output:")
for num in generate_numbers():
    print(num)