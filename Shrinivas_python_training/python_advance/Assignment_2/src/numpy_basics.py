import numpy as np

def main():
    """
    Assignment 1: NumPy Basics
    """

    # Creating NumPy array
    arr = np.array([10, 20, 30, 40, 50])

    print(" Basic Array Statistics ")
    print("Array:", arr)
    print("Mean:", np.mean(arr))
    print("Max:", np.max(arr))
    print("Min:", np.min(arr))
    print("Sum:", np.sum(arr))

    # two arrays for basic operations
    arr_1 = np.array([1, 2, 3])
    arr_2 = np.array([4, 5, 6])

    print("\n Array Operations ")
    print("Array 1:", arr_1)
    print("Array 2:", arr_2)
    print("Addition:", arr_1 + arr_2)
    print("Multiplication:", arr_1 * arr_2)

    # 3x3 matrix
    matrix = np.array([
        [1, 2, 3],
        [4, 5, 6],
        [7, 8, 9]
    ])

    print("\n 3x3 Matrix ")
    print(matrix)


if __name__ == "__main__":
    main()