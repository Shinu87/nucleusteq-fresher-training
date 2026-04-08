package Shrinivas_java_training.session1.advanced;

// interface defines methods must be implemented
interface PaymentMethod {
    void pay(double amount);
}

// class 1
class UPI implements PaymentMethod {

    public void pay(double amount) {
        System.out.println("Paid " + amount + " using UPI");
    }
}

// class 2
class CreditCard implements PaymentMethod {

    public void pay(double amount) {
        System.out.println("Paid " + amount + " using Credit Card");
    }
}

public class InterfaceExample {

    public static void main(String[] args) {

        PaymentMethod p1 = new UPI();
        p1.pay(300);

        PaymentMethod p2 = new CreditCard();
        p2.pay(700);
    }
}