"""
25. Write a regular expression to validate an email address.
"""

import re


def main():
    """
    Main function to validate email using regex.
    """
    email = input("Enter email: ")

    pattern = r"^[a-zA-Z0-9]+@[a-zA-Z0-9]+\.[a-zA-Z]+$"

    if re.match(pattern, email):
        print("Valid Email")
    else:
        print("Invalid Email")


if __name__ == "__main__":
    main()