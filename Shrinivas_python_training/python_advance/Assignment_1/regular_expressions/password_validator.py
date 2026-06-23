"""
31. Create a password validation program using regex (minimum length, one digit, one special character).
"""

# Rules:
# - Minimum 8 characters
# - At least one digit
# - At least one special character

import re


def main():
    """
    Main function to validate password using regex rules.
    """
    password = input("Enter password: ")

    pattern = r"^(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$"

    if re.match(pattern, password):
        print("Valid Password")
    else:
        print("Invalid Password")


if __name__ == "__main__":
    main()