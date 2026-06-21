"""
39. Create a package with two modules and include an __init__.py file.
"""

from employee_package import (
    get_employee_details,
    calculate_annual_salary
)


def main():
    """
    Main function to demonstrate package usage.
    """
    employee = get_employee_details()

    annual_salary = calculate_annual_salary(50000)

    print("Employee Details:")
    print(employee)

    print(f"Annual Salary: {annual_salary}")


if __name__ == "__main__":
    main()