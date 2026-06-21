"""
19. Use filter() to extract even numbers from a list.
"""

def main():
    """
    Main function to filter even numbers from a list.
    """
    numbers = [1, 2, 3, 4, 5]

    even_numbers = filter(lambda x: x % 2 == 0, numbers)

    print("Even Numbers:")

    for num in even_numbers:
        print(num)


if __name__ == "__main__":
    main()