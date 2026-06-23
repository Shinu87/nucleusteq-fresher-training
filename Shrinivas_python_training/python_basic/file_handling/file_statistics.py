"""
36. Read a file and count words, lines, and characters.

- Read mode ("r") is used to read file content.
File Used: sample.txt
"""


def analyze_file(file_name: str) -> tuple[int, int, int]:
    """
    Reads a file and returns:
    (line_count, word_count, character_count)
    """

    lines = 0
    words = 0
    characters = 0

    with open(file_name, "r") as file:
        for line in file:
            lines += 1
            characters += len(line)
            words += len(line.split())

    return lines, words, characters


# MAIN PROGRAM

file_name = "Shrinivas_python_training/python_basic/file_handling/sample.txt"

try:
    line_count, word_count, char_count = analyze_file(file_name)

    print("File Analysis Result:")
    print("Lines:", line_count)
    print("Words:", word_count)
    print("Characters:", char_count)

except FileNotFoundError:
    print("Error: File not found.")