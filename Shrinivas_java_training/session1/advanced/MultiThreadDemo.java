package Shrinivas_java_training.session1.advanced;

// thread 1
class NumberPrinter extends Thread {

    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println("Number: " + i);

            try {
                Thread.sleep(300); // small delay to observe execution
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}

// thread 2
class MessagePrinter extends Thread {

    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println("Processing task...");

            try {
                Thread.sleep(300); // small delay
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}

public class MultiThreadDemo {

    public static void main(String[] args) {

        NumberPrinter t1 = new NumberPrinter();
        MessagePrinter t2 = new MessagePrinter();

        t1.start(); // start first thread
        t2.start(); // start second thread
    }
}