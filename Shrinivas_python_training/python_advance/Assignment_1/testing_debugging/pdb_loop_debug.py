"""
35. Use pdb breakpoints inside a loop and inspect variable values.
"""

import pdb


def main():
    """
    Main function to inspect variable values during loop execution.
    """
    daily_sales = [1200, 1500, 1800, 900, 2000]

    total_revenue = 0

    for day, sale in enumerate(daily_sales, start=1):
        pdb.set_trace()  # Breakpoint inside loop

        total_revenue += sale

        print(f"Day {day}: Revenue = {sale}")

    print(f"Total Revenue: {total_revenue}")


if __name__ == "__main__":
    main()