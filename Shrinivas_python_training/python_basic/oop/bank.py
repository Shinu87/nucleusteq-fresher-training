"""
43. Implement encapsulation using private variables in Bank class.

Bank account balance should not be directly accessible.
It should only be updated using deposit and withdraw methods.
"""


class Bank:
    """
    Simple Bank Account System using Encapsulation.
    """

    def __init__(self, account_holder: str, initial_balance: float = 0.0) -> None:
        self.account_holder = account_holder
        # private variable cannot be accessed directly outside class
        self.__balance = initial_balance  

    def deposit(self, amount: float) -> None:
        """
        Adds money to the account if the amount is valid.
        """

        if amount <= 0:
            print("Deposit amount must be greater than 0.")
            return

        self.__balance += amount
        print(f"{amount} deposited successfully.")

    def withdraw(self, amount: float) -> None:
        """
        Removes money from the account if balance is sufficient.
        """

        if amount <= 0:
            print("Withdrawal amount must be greater than 0.")
            return

        if amount > self.__balance:
            print("Insufficient balance.")
            return

        self.__balance -= amount
        print(f"{amount} withdrawn successfully.")

    def get_balance(self) -> float:
        """
        Returns current balance safely read-only access.
        """

        return self.__balance

    def display_account_info(self) -> None:
        """
        Shows account holder name and current balance.
        """

        print("\nAccount Details")
        print("Account Holder:", self.account_holder)
        print("Current Balance:", self.__balance)


# MAIN PROGRAM

name = input("Enter account holder name: ")
initial_balance = float(input("Enter initial balance: "))

account = Bank(name, initial_balance)

print("\nBanking Operations")

account.deposit(float(input("Enter deposit amount: ")))
account.withdraw(float(input("Enter withdrawal amount: ")))

account.display_account_info()