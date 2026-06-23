"""
48. Convert a normal function into parallel execution using ProcessPoolExecutor.
"""

from concurrent.futures import ProcessPoolExecutor
import time


def process_task(number: int) -> int:
    """
    Simulate a CPU bound task and return square of a number.
    """
    time.sleep(1)  # simulate processing delay
    return number * number


def main():
    """
    Main function to demonstrate ProcessPoolExecutor.
    """

    numbers = [1, 2, 3, 4, 5]

    print("Starting ProcessPoolExecutor...\n")

    with ProcessPoolExecutor(max_workers=3) as executor:
        results = executor.map(process_task, numbers)

    for number, result in zip(numbers, results):
        print(f"Square of {number}: {result}")

    print("\nAll processes completed execution")


if __name__ == "__main__":
    main()