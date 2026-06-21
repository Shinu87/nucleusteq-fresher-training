"""
46. Write a multiprocessing program to calculate the square of numbers using Process class.
"""

import multiprocessing


def calculate_square(number: int) -> None:
    """
    Calculates and prints the square of a number.
    """
    print(f"Square of {number}: {number * number}")


def main():
    """
    Main function to create multiple processes for square calculation.
    """

    numbers = [1, 2, 3, 4, 5]

    processes = []

    for number in numbers:
        process = multiprocessing.Process(
            target=calculate_square,
            args=(number,)
        )
        processes.append(process)
        process.start()

    for process in processes:
        process.join()

    print("All processes completed execution")


if __name__ == "__main__":
    main()