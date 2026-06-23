"""
43. Demonstrate the use of join() method in threading.
"""

import threading
import time


def task(name: str, delay: int) -> None:
    """
    Simulate a task using sleep for a few seconds.
    """
    print(f"{name} started")

    time.sleep(delay)

    print(f"{name} finished")


def main():
    """
    Main function to demonstrate join() in threading.
    """

    thread1 = threading.Thread(target=task, args=("Thread-1", 2))
    thread2 = threading.Thread(target=task, args=("Thread-2", 3))

    thread1.start()
    thread2.start()

    # join() makes the main thread wait until all threads finish
    thread1.join()
    thread2.join()

    print("Both threads have completed execution")


if __name__ == "__main__":
    main()