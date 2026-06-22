"""
Assignment 5: Matplotlib Charts

Tasks:
1. Create Bar Chart
2. Create Line Chart
3. Create Histogram
4. Create Scatter Plot (Age vs Salary)
"""

import pandas as pd
import matplotlib.pyplot as plt


def main():
    """Program to create different charts using Matplotlib."""

    department_df = pd.DataFrame({
        "Department": ["HR", "IT", "Finance"],
        "Employees": [5, 12, 7]
    })

    employee_df = pd.DataFrame({
        "Age": [25, 30, 28, 35, 29],
        "Salary": [30000, 40000, 50000, 60000, 45000]
    })

    # Create bar Chart
    plt.figure(figsize=(6, 4))
    plt.bar(department_df["Department"], department_df["Employees"])
    plt.title("Employees by Department")
    plt.xlabel("Department")
    plt.ylabel("Employees")
    plt.show()

    # Create line Chart
    plt.figure(figsize=(6, 4))
    plt.plot(
        department_df["Department"],
        department_df["Employees"],
        marker="o"
    )
    plt.title("Employees by Department")
    plt.xlabel("Department")
    plt.ylabel("Employees")
    plt.show()

    # Create histogram for salary data
    plt.figure(figsize=(6, 4))
    plt.hist(
        employee_df["Salary"],
        bins=[30000, 40000, 50000, 60000, 70000]
    )
    plt.title("Salary Distribution")
    plt.xlabel("Salary")
    plt.ylabel("Frequency")
    plt.show()

    # Create scatter plot between age and salary
    plt.figure(figsize=(6, 4))
    plt.scatter(
        employee_df["Age"],
        employee_df["Salary"]
    )
    plt.title("Age vs Salary")
    plt.xlabel("Age")
    plt.ylabel("Salary")
    plt.show()


if __name__ == "__main__":
    main()