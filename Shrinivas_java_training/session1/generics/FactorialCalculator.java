package Shrinivas_java_training.session1.generics;

import java.util.Scanner;

public class FactorialCalculator {

    // Recursive method to calculate factorial
    public static int getFactorial(int number) {
        if (number == 1) {
            return 1; // base case
        }
        return number * FactorialCalculator.getFactorial(number - 1); // recursive call
    }

    // Iterative method to calculate factorial using loop
    public static int getFactorialUsingLoop(int number) {
        int factorial = 1;
        for (int i = number; i >= 1; i--) {
            factorial = factorial * i;
        }
        return factorial;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a number to calculate factorial:");
        int number = scanner.nextInt();

        // Using recursive method
        int factorial = FactorialCalculator.getFactorial(number);
        System.out.println("Factorial (recursive): " + factorial);

        // Using iterative method
        int factorialLoop = FactorialCalculator.getFactorialUsingLoop(number);
        System.out.println("Factorial (loop): " + factorialLoop);

        scanner.close();
    }
}