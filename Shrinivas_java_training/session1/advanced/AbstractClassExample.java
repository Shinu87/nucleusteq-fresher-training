// abstract class used to define common structure for all payment types
// different payment methods implement it in their own way

package Shrinivas_java_training.session1.advanced;

abstract class Payment {

    // abstract method (no body)
    abstract void makePayment(double amount);

    // normal method
    void paymentInfo() {
        System.out.println("Processing payment...");
    }
}

// child class 1
class UPIPayment extends Payment {

    void makePayment(double amount) {
        System.out.println("Payment of " + amount + " done using UPI");
    }
}

// child class 2
class CardPayment extends Payment {

    void makePayment(double amount) {
        System.out.println("Payment of " + amount + " done using Card");
    }
}

public class AbstractClassExample {

    public static void main(String[] args) {

        // using parent reference
        Payment p1 = new UPIPayment();
        p1.paymentInfo();
        p1.makePayment(500);

        Payment p2 = new CardPayment();
        p2.paymentInfo();
        p2.makePayment(1000);
    }
}