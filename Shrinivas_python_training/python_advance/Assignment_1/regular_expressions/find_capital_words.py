"""
28. Use re.findall() to extract all words starting with a capital letter.
"""

import re


def main():
    """
    Main function to extract capitalized words from a sentence.
    """
    sentence = input("Enter a sentence: ")

    pattern = r"\b[A-Z]\w*\b"
    
    capital_words = re.findall(pattern, sentence)

    print("Capital Words:", capital_words)


if __name__ == "__main__":
    main()