"""
44. Demonstrate polymorphism using different classes with the same method name.

Different payment methods (UPI, Card, Cash) all use the same method name pay()
but behave differently.

"""


class PaymentMethod:
    """
    Base class for payment methods.
    """

    def pay(self, amount: float) -> None:
        """
        Generic payment method - to be overridden.
        """
        print(f"Paying {amount} using generic method")


class UPI(PaymentMethod):
    """
    Payment using UPI.
    """

    def pay(self, amount: float) -> None:
        print(f"Paid {amount} using UPI")


class CreditCard(PaymentMethod):
    """
    Payment using Credit Card.
    """

    def pay(self, amount: float) -> None:
        print(f"Paid {amount} using Credit Card")


class Cash(PaymentMethod):
    """
    Payment using Cash.
    """

    def pay(self, amount: float) -> None:
        print(f"Paid {amount} using Cash")


# MAIN PROGRAM

amount = float(input("Enter payment amount: "))

methods = [
    UPI(),
    CreditCard(),
    Cash()
]

print("\n Processing Payments ")

for method in methods:
    method.pay(amount)