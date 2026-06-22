"""
Assignment 2: Pandas DataFrame Creation
"""

import pandas as pd

def main():

    # Creating employee dataframe
    df = pd.DataFrame({
        "Name": ["Rahul", "Priya", "Amit", "Anuj"],
        "Age": [25, 30, 28, 35],
        "Department": ["HR", "IT", "Finance", "IT"],
        "Salary": [30000, 50000, 45000, 60000]
    })

    print(" Full Employee Data ")
    print(df)

    # First 2 rows
    print("\n First 2 Rows ")
    print(df.head(2))

    # Summary statistics
    print("\n Summary Statistics ")
    print(df.describe())

    # Only IT employees
    print("\n IT Employees ")
    print(df[df["Department"] == "IT"])

    # Adding bonus column (10%)
    df["Bonus"] = df["Salary"] * 0.10

    print("\n Data with Bonus Column ")
    print(df)


if __name__ == "__main__":
    main()