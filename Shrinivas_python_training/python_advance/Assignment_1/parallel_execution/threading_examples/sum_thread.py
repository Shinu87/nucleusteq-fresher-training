"""
42. Create a thread that calculates the sum of numbers from 1 to 100.
"""

import threading


def calculate_sum() -> None:
    """
    Find sum of numbers from 1 to 100.
    """
    total_sum = 0

    for number in range(1, 101):
        total_sum += number

    print(f"Sum of numbers from 1 to 100: {total_sum}")


def main():
    """
    Main function to create and run thread.
    """
    thread = threading.Thread(target=calculate_sum)

    thread.start()
    thread.join()


if __name__ == "__main__":
    main()