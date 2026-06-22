"""
Assignment 6: Seaborn Visualizations

Tasks:
1. Barplot - Department vs Salary
2. Boxplot - Salary Distribution
3. Heatmap - Correlation between Age and Salary
"""

import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt


def main():
    """Program to create charts using Seaborn."""

    df = pd.read_csv(
        "Shrinivas_python_training/python_advance/Assignment_2/data/employee_data.csv"
    )

    # Create barplot for department and salary
    plt.figure(figsize=(6, 4))
    sns.barplot(data=df, x="Department", y="Salary")
    plt.title("Department vs Salary")
    plt.show()

    # Create boxplot for salary distribution
    plt.figure(figsize=(6, 4))
    sns.boxplot(data=df, y="Salary")
    plt.title("Salary Distribution")
    plt.show()

    # Find correlation between age and salary
    plt.figure(figsize=(6, 4))
    correlation_matrix = df[["Age", "Salary"]].corr()

    # Create heatmap using correlation values
    sns.heatmap(
        correlation_matrix,
        annot=True,
        cmap="Blues"
    )

    plt.title("Correlation Between Age and Salary")
    plt.show()


if __name__ == "__main__":
    main()