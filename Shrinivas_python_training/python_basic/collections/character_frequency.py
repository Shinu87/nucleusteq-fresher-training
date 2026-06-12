"""
33. Count frequency of characters in a string using dictionary.

Approach:
Using Dictionary
   - Go through each character in the string one by one.
   - Store each character as a key in a dictionary.
   - Increase its count whenever it appears again.
   - Time Complexity: O(n)
   - Space Complexity: O(k)

   where:
   n = length of the string
   k = number of unique characters
"""

def count_character_frequency(text: str) -> dict[str, int]:
    frequency = {}

    for character in text:
        frequency[character] = frequency.get(character, 0) + 1

    return frequency


# MAIN PROGRAM

text = input("Enter a string: ")

character_frequency = count_character_frequency(text)

print("\nCharacter Frequency:")

for character, count in character_frequency.items():
    print(f"{character}: {count}")