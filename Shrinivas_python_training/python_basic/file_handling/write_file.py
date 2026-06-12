"""
35. Create a file and write your name into it.

- 'w' mode is used to write into a file.
- If file does not exist it will be created.
- If file already exists its content will be overwritten.

"""


def write_name_to_file(file_name: str, name: str) -> None:
    """
    Writes the given name into a file.
    """

    with open(file_name, "w") as file:
        file.write(name)


# MAIN PROGRAM

file_name =  "Shrinivas_python_training/python_basic/file_handling/student.txt"
name = input("Enter your name: ")

write_name_to_file(file_name, name)

print(f"Name written successfully into {file_name}")