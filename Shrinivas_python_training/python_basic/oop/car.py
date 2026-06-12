"""
41. Create a Car class with a constructor.
"""


class Car:
    """
    Represents a car with basic properties.
    """

    def __init__(self, brand: str, model: str, year: int, price: float) -> None:
        """
        Constructor to initialize car attributes.
        """

        self.brand = brand
        self.model = model
        self.year = year
        self.price = price

    def display_car_details(self) -> None:
        """
        Displays car details.
        """

        print("Car Details")
        print("Brand:", self.brand)
        print("Model:", self.model)
        print("Year:", self.year)
        print("Price:", self.price)


# MAIN PROGRAM

brand = input("Enter car brand: ")
model = input("Enter car model: ")
year = int(input("Enter manufacturing year: "))
price = float(input("Enter car price: "))

car = Car(brand, model, year, price)

print()
car.display_car_details()