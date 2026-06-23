"""
26. Write a regular expression to validate a 10-digit mobile number.
"""

import re


def main():
    """
    Main function to validate a 10-digit mobile number.
    """
    number = input("Enter mobile number: ")

    pattern = r"^\d{10}$"

    if re.match(pattern, number):
        print("Valid Mobile Number")
    else:
        print("Invalid Mobile Number")


if __name__ == "__main__":
    main()