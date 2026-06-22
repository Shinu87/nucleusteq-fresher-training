"""
Assignment 3: Data Cleaning
"""

import pandas as pd

def main():

    # Creating dataset as per assignment
    df = pd.DataFrame({
        "Name": ["Rahul", "Priya", "Anuj"],
        "Age": [25, None, 29],
        "Salary": [30000, 40000, None]
    })

    print(" Original Dataset ")
    print(df)

    # Checking missing values
    print("\n Missing Values Count ")
    print(df.isnull().sum())

    # Filling missing age with mean value
    age_mean = df["Age"].mean()
    df["Age"] = df["Age"].fillna(age_mean)

    # Filling missing salary with 0
    df["Salary"] = df["Salary"].fillna(0)

    print("\n Cleaned Dataset ")
    print(df)


if __name__ == "__main__":
    main()