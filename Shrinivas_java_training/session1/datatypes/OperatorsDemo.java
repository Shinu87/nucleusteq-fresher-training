package Shrinivas_java_training.session1.datatypes;

import java.util.Scanner;

public class OperatorsDemo {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter first number: ");
        int num1 = scanner.nextInt();
        System.out.print("Enter second number: ");
        int num2 = scanner.nextInt();
        System.out.println("");

        // Arithmetic operators
        System.out.println("Arithmetic Operators : ");
        System.out.println("Addition: " + (num1 + num2));
        System.out.println("Subtraction: " + (num1 - num2));
        System.out.println("Multiplication: " + (num1 * num2));
        System.out.println("Division: " + (num1 / num2));
        System.out.println("Modulus: " + (num1 % num2));
        System.out.println("");

        // Relational operators
        System.out.println("Relational Operators : ");
        System.out.println("Is num1 > num2? " + (num1 > num2));
        System.out.println("Is num1 < num2? " + (num1 < num2));
        System.out.println("Is num1 == num2? " + (num1 == num2));
        System.out.println("Is num1 != num2? " + (num1 != num2));
        System.out.println("");

        // Logical operators
        System.out.println("Logical Operators : ");
        boolean cond1 = (num1 > 0) ? true : false;
        boolean cond2 = (num2 > 0) ? true : false;
        System.out.println("num1 > 0 AND num2 > 0: " + (cond1 && cond2));
        System.out.println("num1 > 0 OR num2 > 0: " + (cond1 || cond2));
        System.out.println("NOT (num1 > 0): " + (!cond1));

        scanner.close();
    }
}