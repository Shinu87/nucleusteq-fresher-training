"""
39. Search a word in a file.

- Read file line by line.
- Check if the given word exists in each line.
- Track line numbers where the word is found.

"""


def search_word_in_file(file_name: str, word: str) -> list[int]:
    """
    Searches for a word in a file and returns line numbers.
    """

    line_numbers = []
    current_line = 0

    with open(file_name, "r") as file:
        for line in file:
            current_line += 1

            if word.lower() in line.lower():
                line_numbers.append(current_line)

    return line_numbers


# MAIN PROGRAM

file_name = "sample.txt"
word = input("Enter word to search: ")

try:
    result = search_word_in_file(file_name, word)

    if result:
        print(f"\nWord '{word}' found in line(s):", result)
    else:
        print(f"\nWord '{word}' not found in file.")

except FileNotFoundError:
    print("Error: File not found.")