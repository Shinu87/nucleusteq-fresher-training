"""
Assignment 7: Mini Project

Tasks:
1. Load student data into Pandas
2. Add Performance column
3. Create Line Chart (Hours_Studied vs Marks)
4. Create Scatter Plot (Hours_Studied vs Marks)
5. Create Seaborn Barplot (Performance vs Marks)
"""

import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns


def main():
    """Mini project for student performance analysis."""

    # Create student dataset
    student_df = pd.DataFrame({
        "Name": ["Rahul", "Priya", "Siri", "Anuj"],
        "Marks": [70, 80, 90, 60],
        "Hours_Studied": [2, 3, 5, 1]
    })

    # Add performance column based on marks
    student_df["Performance"] = student_df["Marks"].apply(
        lambda marks: "Pass" if marks > 65 else "Fail"
    )

    print(" Student Dataset ")
    print(student_df)

    # Create line chart for hours studied and marks
    plt.figure(figsize=(6, 4))
    plt.plot(
        student_df["Hours_Studied"],
        student_df["Marks"],
        marker="o"
    )
    plt.title("Hours Studied vs Marks")
    plt.xlabel("Hours_Studied")
    plt.ylabel("Marks")
    plt.show()

    # Create scatter plot for hours studied and marks
    plt.figure(figsize=(6, 4))
    plt.scatter(
        student_df["Hours_Studied"],
        student_df["Marks"]
    )
    plt.title("Hours Studied vs Marks")
    plt.xlabel("Hours Studied")
    plt.ylabel("Marks")
    plt.show()

    # Create seaborn barplot for performance and marks
    plt.figure(figsize=(6, 4))
    sns.barplot(
        data=student_df,
        x="Performance",
        y="Marks"
    )
    plt.title("Performance vs Marks")
    plt.show()


if __name__ == "__main__":
    main()