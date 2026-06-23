"""
23. Convert a simple loop-based program into a functional style using map or filter.
"""

def main():
    """
    Show loop-based and map-based approach for the same problem.
    """

    names = ["shinu", "mohan", "rahul", "anita"]

    # 1. Loop-based approach
    loop_result = []

    for name in names:
        loop_result.append(name.upper())

    print("Loop Output:", loop_result)

    # 2. Functional approach (map)
    functional_result = list(map(lambda name: name.upper(), names))

    print("Functional Output:", functional_result)


if __name__ == "__main__":
    main()