"""
20. Write a function using default parameters.
"""

def calculate_salary(basic_salary: float,hra_percentage: float = 20,bonus: float = 5000) -> float:
    """
    Calculates total salary using default HRA percentage and bonus.

    Time Complexity: O(1)
    Space Complexity: O(1)
    """

    if basic_salary < 0 or hra_percentage < 0 or bonus < 0:
        return 0

    hra_amount = basic_salary * hra_percentage / 100

    return basic_salary + hra_amount + bonus


# MAIN PROGRAM

basic_salary = float(input("Enter basic salary: "))

if basic_salary < 0:
    print("Salary cannot be negative.")
else:
    print("\nUsing Default Parameters:")
    print("Total Salary:", calculate_salary(basic_salary))

    custom_hra = float(input("\nEnter custom HRA percentage: "))
    custom_bonus = float(input("Enter custom bonus: "))

    if custom_hra < 0 or custom_bonus < 0:
        print("HRA percentage and bonus cannot be negative.")
    else:
        print("\nUsing Custom Parameters:")
        print("Total Salary:", calculate_salary(basic_salary,custom_hra,custom_bonus))