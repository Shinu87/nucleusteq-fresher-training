"""
24. Create your own module and import it.
"""

from custom_module import find_cube, find_area_of_square

# MAIN PROGRAM

number = int(input("Enter a number: "))
side = float(input("Enter side of square: "))

print("\nCube:", find_cube(number))

if side < 0:
    print("Side length cannot be negative.")
else:
    print("Area of Square:", find_area_of_square(side))