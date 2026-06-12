"""
42. Implement inheritance using Person and Employee class.
"""


class Person:
    """
    Base class representing a generic person.
    """

    def __init__(self, name: str, age: int, gender: str) -> None:
        self.name = name
        self.age = age
        self.gender = gender

    def display_basic_info(self) -> None:
        """
        Displays basic person information.
        """

        print("Personal Details")
        print("Name:", self.name)
        print("Age:", self.age)
        print("Gender:", self.gender)


class Employee(Person):
    """
    Employee class inherits from Person.
    """

    def __init__(self, name: str, age: int, gender: str,employee_id: int, department: str, salary: float) -> None:

        super().__init__(name, age, gender)

        self.employee_id = employee_id
        self.department = department
        self.salary = salary

    def display_employee_info(self) -> None:
        """
        Displays employee details.
        """

        self.display_basic_info()

        print("\nEmployee Details")
        print("Employee ID:", self.employee_id)
        print("Department:", self.department)
        print("Salary:", self.salary)


# MAIN PROGRAM

name = input("Enter name: ")
age = int(input("Enter age: "))
gender = input("Enter gender: ")

employee_id = int(input("Enter employee ID: "))
department = input("Enter department: ")
salary = float(input("Enter salary: "))

employee = Employee(name, age, gender, employee_id, department, salary)

print()
employee.display_employee_info()