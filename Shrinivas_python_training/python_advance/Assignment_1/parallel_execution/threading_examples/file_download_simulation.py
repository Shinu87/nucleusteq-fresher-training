"""
44. Create multiple threads to simulate file downloading using time.sleep().
"""

import threading
import time


def download_file(file_name: str, download_time: int) -> None:
    """
    Simulate file download using sleep.
    """
    print(f"{file_name} started downloading...")

    for progress in range(1, 6):
        time.sleep(download_time)
        print(f"{file_name} downloading... {progress * 20}% completed")

    print(f"{file_name} downloaded successfully!")


def main():
    """
    Main function to create multiple threads for file download simulation.
    """

    files = [
        ("File_A.zip", 1),
        ("File_B.zip", 2),
        ("File_C.zip", 1)
    ]

    threads = []

    for file_name, delay in files:
        thread = threading.Thread(
            target=download_file,
            args=(file_name, delay)
        )
        threads.append(thread)
        thread.start()

    # Make sure all downloads finish before the main program exits
    for thread in threads:
        thread.join()

    print("All files downloaded successfully!")


if __name__ == "__main__":
    main()