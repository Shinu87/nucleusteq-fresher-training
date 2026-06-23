"""
38. Copy content from one file to another.

- Read content from a source file.
- Write that content into a destination file.
- 'r' mode → read file
- 'w' mode → write file

"""


def copy_file(source_file: str, destination_file: str) -> None:
    """
    Copies content from source file to destination file.
    """

    with open(source_file, "r") as src:
        content = src.read()

    with open(destination_file, "w") as dest:
        dest.write(content)


# MAIN PROGRAM
source_file = "Shrinivas_python_training/python_basic/file_handling/sample.txt"
destination_file =  "Shrinivas_python_training/python_basic/file_handling/copy.txt"

try:
    copy_file(source_file, destination_file)
    print(f"Content copied from {source_file} to {destination_file}")

except FileNotFoundError:
    print("Error: Source file not found.")