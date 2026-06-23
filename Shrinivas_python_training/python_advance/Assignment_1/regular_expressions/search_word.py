"""
27. Use re.search() to check whether a word exists in a sentence.
"""

import re


def main():
    """
    Main function to search a word in a sentence using regex.
    """
    sentence = input("Enter a sentence: ")
    word = input("Enter word to search: ")

    pattern = rf"\b{word}\b"

    if re.search(pattern, sentence):
        print("Word Found")
    else:
        print("Word Not Found")


if __name__ == "__main__":
    main()