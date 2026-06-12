"""
27. Reverse a list without using reverse().

Approaches:

1. Using Two Pointers
   - Start with one pointer at the beginning and another at the end.
   - Swap the elements and move both pointers towards the center.
   - Time Complexity: O(n)
   - Space Complexity: O(1)

2. Using List Slicing
   - Use numbers[::-1] to get the reversed list.
   - Time Complexity: O(n)
   - Space Complexity: O(n)

3. Using a Loop
   - Traverse the list from the last element to the first.
   - Store the elements in a new list.
   - Time Complexity: O(n)
   - Space Complexity: O(n)

The two-pointer approach is used here because it reverses the list without creating an extra list.
"""


def reverse_list(numbers: list[int]) -> list[int]:

    left = 0
    right = len(numbers) - 1

    while left < right:
        numbers[left], numbers[right] = numbers[right], numbers[left]

        left += 1
        right -= 1

    return numbers


# MAIN PROGRAM

numbers = list(map(int,input("Enter numbers separated by spaces: ").split()))

print("\nOriginal List:", numbers)
print("Reversed List:", reverse_list(numbers))