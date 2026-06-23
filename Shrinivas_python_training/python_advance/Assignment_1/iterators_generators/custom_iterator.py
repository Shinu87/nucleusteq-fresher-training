"""
10. Write a custom iterator class that returns numbers from 1 to N.
"""

class NumberIterator:
    """
    Iterator to print numbers from 1 to N.
    """

    def __init__(self, limit):
        self.limit = limit
        self.current = 1

    def __iter__(self):
        return self

    def __next__(self):
        if self.current <= self.limit:
            value = self.current
            self.current += 1
            return value

        raise StopIteration


if __name__ == "__main__":
    number_iterator = NumberIterator(10)

    for number in number_iterator:
        print(number)