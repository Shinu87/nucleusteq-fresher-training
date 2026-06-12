"""
37. Append data to an existing file.

- 'a' mode is used to append data to an existing file.
- If file does not exist it will be created.
- Existing content is NOT overwritten.

"""


def append_to_file(file_name: str, data: str) -> None:
    """
    Appends the given data into a file.
    """

    with open(file_name, "a") as file:
        file.write("\n" + data)


# MAIN PROGRAM

file_name = "Shrinivas_python_training/python_basic/file_handling/sample.txt"

data = input("Enter text to append into file: ")

append_to_file(file_name, data)

print("Data appended successfully!")