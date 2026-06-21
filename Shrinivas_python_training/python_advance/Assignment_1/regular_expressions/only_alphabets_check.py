"""
30. Write a pattern to check if a string contains only alphabets.
"""

import re


def main():
    """
    Main function to validate if input contains only alphabets.
    """
    text = input("Enter a string: ")

    pattern = r"^[A-Za-z]+$"

    if re.match(pattern, text):
        print("Valid: Only Alphabets")
    else:
        print("Invalid: Contains non-alphabet characters")


if __name__ == "__main__":
    main()