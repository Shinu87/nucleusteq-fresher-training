package Shrinivas_java_training.session1.generics;

import java.util.Scanner;

public class FibonacciGenerator {

    // Recursive method to calculate nth Fibonacci number
    public static int calculateFibonacci(int limit, int a, int b) {
        if (limit == 0) {
            return a; // base case
        }
        return calculateFibonacci(limit - 1, b, a + b); // recursive step
    }

    // Iterative method to calculate nth Fibonacci number
    public static int calculateFibonacciUsingLoop(int limit) {
        int a = 0;
        int b = 1;
        int c;
        for (int i = 0; i < limit; i++) {
            c = b;
            b = a + b;
            a = c;
        }
        return a;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter how many Fibonacci numbers to generate: ");
        int limit = scanner.nextInt();

        // Using recursive method
        System.out.println("Fibonacci (recursive) up to " + limit + "th number: "
                + calculateFibonacci(limit, 0, 1));

        // Using iterative method
        System.out.println("Fibonacci (loop) up to " + limit + "th number: "
                + calculateFibonacciUsingLoop(limit));

        scanner.close();
    }
}