package Shrinivas_java_training.session1.controlflow;

import java.util.Scanner;

public class MultiplicationTable {

    // print multiplication table of a number
    public static void getMultiplication(int num) {
        for (int i = 1; i <= 10; i++) {
            System.out.println(num + " * " + i + " = " + (num * i));
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter a number: ");

        if (!scanner.hasNextInt()) {
            System.out.println("Please enter an integer.");
            scanner.close();
            return;
        }

        int num = scanner.nextInt();

        if (num == 0) {
            System.out.println("Multiplication table of 0 will always be 0");
        }

        getMultiplication(num);

        scanner.close();
    }
}