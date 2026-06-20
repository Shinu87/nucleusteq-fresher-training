"""
8. Write a program that handles FileNotFoundError when trying to open a file.
"""


FILE_PATH = "sample_files/missing_file.txt"


def read_file() -> None:
    """
    Attempts to open a file and handles FileNotFoundError.
    """
    try:
        with open(FILE_PATH, "r") as source_file:
            content = source_file.read()
            print(content)

    except FileNotFoundError:
        print("File not found.")


read_file()