"""
37. Create a module with two utility functions and import it into another Python file.
"""

from utilities import reverse_text, count_vowels


def main():
    """
    Main function to demonstrate utility functions.
    """
    text = input("Enter text: ")

    print(f"Reversed Text: {reverse_text(text)}")
    print(f"Vowel Count: {count_vowels(text)}")


if __name__ == "__main__":
    main()