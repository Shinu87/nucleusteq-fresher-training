"""
3. Write a program using try-except-else-finally to read a number from a file and print its square.
"""

def print_square_from_file() -> None:
    """
    Reads a number from a file and prints its square.
    """
    FILE_PATH = "Shrinivas_python_training/python_advance/Assignment_1/sample_files/number.txt"
    try:
        with open(FILE_PATH, "r") as src:
            number = src.read().strip()
            number = int(number)

    except FileNotFoundError:
        print("File not found.")

    except ValueError:
        print("The file does not contain a valid integer.")

    else:
        print(f"Square of the number: {number ** 2}")

    finally:
        print("File operation completed.")


print_square_from_file()