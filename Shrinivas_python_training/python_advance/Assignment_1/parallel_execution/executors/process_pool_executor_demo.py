"""
47. Convert a normal function into parallel execution using ThreadPoolExecutor.
"""

from concurrent.futures import ThreadPoolExecutor
import time


def process_task(number: int) -> int:
    """
    Simulate a task and return square of a number.
    """
    time.sleep(1)  #simulate waiting time for I/O operation
    return number * number


def main():
    """
    Main function to demonstrate ThreadPoolExecutor.
    """

    numbers = [1, 2, 3, 4, 5]

    print("Starting ThreadPoolExecutor...\n")

    with ThreadPoolExecutor(max_workers=3) as executor:
        results = executor.map(process_task, numbers)

    for number, result in zip(numbers, results):
        print(f"Square of {number}: {result}")

    print("\nAll tasks completed")


if __name__ == "__main__":
    main()