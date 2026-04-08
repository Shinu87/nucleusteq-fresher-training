package Shrinivas_java_training.session1.generics;

import java.util.Scanner;

public class EvenOddChecker {

    // Method to check if a number is even using modulo
    public static boolean checkEvenOrOdd(int number) {
        if (number % 2 == 0) {
            return true; // even
        } else {
            return false; // odd
        }
    }

    // Method to check if a number is even using bit manipulation
    public static boolean checkEvenOrOddBit(int number) {
        // If last bit is 0 then even else odd
        return (number & 1) == 0;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // create scanner for input

        System.out.println("Enter a number to check even or odd:");
        int number = scanner.nextInt(); // read user input

        // Using simple modulo method
        boolean isEven = checkEvenOrOdd(number);
        if (isEven) {
            System.out.println(number + " is even (checked using modulo)");
        } else {
            System.out.println(number + " is odd (checked using modulo)");
        }

        // Using optimized bit manipulation method
        boolean isEvenBit = checkEvenOrOddBit(number);
        if (isEvenBit) {
            System.out.println(number + " is even (checked using bit manipulation)");
        } else {
            System.out.println(number + " is odd (checked using bit manipulation)");
        }

        scanner.close(); // close scanner
    }
}