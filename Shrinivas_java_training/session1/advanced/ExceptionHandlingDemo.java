// handling invalid input and division by zero using try-catch

package Shrinivas_java_training.session1.advanced;

import java.util.Scanner;
import java.util.InputMismatchException;

public class ExceptionHandlingDemo {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter a number: ");
            int num = scanner.nextInt();

            int result = 100 / num; // may cause exception if num = 0

            System.out.println("Result: " + result);

        } catch (InputMismatchException e) {
            System.out.println("Please enter a number(Integer).");
        } catch (ArithmeticException e) {
            System.out.println("Cannot divide by zero.");
        } finally {
            System.out.println("Program executed.");
            scanner.close();
        }
    }
}