package Shrinivas_java_training.session1.controlflow;

import java.util.Scanner;

public class PrimeChecker {

    // Method to check if a number is prime
    public static boolean checkPrime(int num) {
        if (num <= 1) {
            return false;
        }

        // Check divisibility from 2 up to sqrt(num) for efficiency
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter a number to check: ");

        if (!scanner.hasNextInt()) {
            System.out.println("Please enter an integer.");
            scanner.close();
            return;
        }

        int num = scanner.nextInt();

        boolean isPrime = checkPrime(num);

        if (isPrime) {
            System.out.println(num + " is a prime number");
        } else {
            System.out.println(num + " is not a prime number.");
        }

        scanner.close();
    }
}