"""
29. Replace multiple spaces in a string with a single space using re.sub().
"""

import re


def main():
    """
    Main function to normalize spaces in a string.
    """
    text = input("Enter a string: ")

    pattern = r"\s+"

    cleaned_text = re.sub(pattern, " ", text).strip()

    print("Cleaned String:", cleaned_text)


if __name__ == "__main__":
    main()