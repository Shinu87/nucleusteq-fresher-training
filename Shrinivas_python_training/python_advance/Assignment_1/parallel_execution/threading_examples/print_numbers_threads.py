"""
41. Write a program to create two threads that print numbers from 1 to 5 simultaneously.
"""
import threading
import time


def print_numbers(thread_name: str) -> None:
    """
    Prints numbers from 1 to 5.
    """
    for number in range(1, 6):
        print(f"{thread_name}: {number}")

        # Small delay added to clearly show thread switching and simultaneous execution of both threads.
        time.sleep(0.1)


def main() -> None:
    """
    Main function to create and execute two threads.
    """
    first_thread = threading.Thread(
        target=print_numbers,
        args=("Thread-1",)
    )

    second_thread = threading.Thread(
        target=print_numbers,
        args=("Thread-2",)
    )

    first_thread.start()
    second_thread.start()

    first_thread.join()
    second_thread.join()

    print("Both threads have completed execution.")


if __name__ == "__main__":
    main()