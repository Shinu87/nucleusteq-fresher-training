"""
45. Write a program to create two processes that print their Process IDs.
"""

import multiprocessing
import os


def print_process_info(process_name: str) -> None:
    """
    Prints process name and its Process ID (PID).
    """
    print(f"{process_name} started")
    print(f"{process_name} PID: {os.getpid()}")


def main():
    """
    Main function to create and run multiple processes.
    """

    process1 = multiprocessing.Process(
        target=print_process_info,
        args=("Process-1",)
    )

    process2 = multiprocessing.Process(
        target=print_process_info,
        args=("Process-2",)
    )

    process1.start()
    process2.start()

    process1.join()
    process2.join()

    print("Both processes completed execution")


if __name__ == "__main__":
    main()