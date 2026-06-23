"""
26. Count even and odd numbers in a list.

Approaches:

1. Using a Loop
   - Go through each element in the list.
   - Check whether the number is even or odd.
   - Increase the corresponding counter.
   - Time Complexity: O(n)
   - Space Complexity: O(1)

2. Using List Comprehension
   - Create separate lists for even and odd numbers.
   - Count the number of elements in each list.
   - Time Complexity: O(n)
   - Space Complexity: O(n)

The loop approach is better because it uses less memory.
"""


def count_even_odd(numbers: list[int]) -> tuple[int, int]:
    even_count = 0
    odd_count = 0

    for number in numbers:
        if number % 2 == 0:
            even_count += 1
        else:
            odd_count += 1

    return even_count, odd_count


# MAIN PROGRAM

numbers = list(map(int,input("Enter numbers separated by spaces: ").split()))

even_count, odd_count = count_even_odd(numbers)

print("\nEven Numbers:", even_count)
print("Odd Numbers:", odd_count)