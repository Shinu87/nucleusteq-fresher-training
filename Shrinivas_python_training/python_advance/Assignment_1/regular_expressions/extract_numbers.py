"""
24. Write a program to extract all numbers from a given string using regular expressions.
"""

import re


def main():
    """
    Main function to extract all numbers from a given string.
    """
    text = input("Enter a string: ")

    numbers = re.findall(r"\d+", text)

    print("Extracted Numbers:", numbers)


if __name__ == "__main__":
    main()