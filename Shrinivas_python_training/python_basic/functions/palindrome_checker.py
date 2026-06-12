"""
18. Write a function to check palindrome (Number and String).
"""


def is_palindrome(value: str) -> bool:
    """
    Checks whether the given value is a palindrome using two pointers.

    Time Complexity: O(n)
    Space Complexity: O(1)
    """

    value = str(value)

    left = 0
    right = len(value) - 1

    while left < right:
        if value[left] != value[right]:
            return False

        left += 1
        right -= 1

    return True


# MAIN PROGRAM

user_input = input("Enter a string or number: ")

if is_palindrome(user_input):
    print("Palindrome")
else:
    print("Not a Palindrome")