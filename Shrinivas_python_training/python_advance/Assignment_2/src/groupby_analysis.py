"""
Assignment 4: Data Analysis
"""

import pandas as pd

def main():

    df = pd.read_csv("Shrinivas_python_training/python_advance/Assignment_2/data/employee_data.csv")
    print(" Employee Dataset ")
    print(df)

    # Calculate average salary department wise
    print("\n Average Salary by Department ")
    print(df.groupby("Department")["Salary"].mean())

    # Find highest salary in each department
    print("\n Maximum Salary by Department ")
    print(df.groupby("Department")["Salary"].max())

    # Count employees in each department
    print("\n Employee Count by Department ")
    print(df.groupby("Department")["Name"].count())


if __name__ == "__main__":
    main()