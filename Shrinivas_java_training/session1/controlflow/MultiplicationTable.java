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
        int num = scanner.nextInt();

        getMultiplication(num);

        scanner.close();
    }
}