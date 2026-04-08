package Shrinivas_java_training.session1.generics;

import java.util.Scanner;

public class PatternPrinter {

    public static void printTriangle(int rows) {
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= rows - i; j++) {
                System.out.print(" ");
            }
            for (int k = 1; k <= i; k++) {
                System.out.print("* ");
            }
            System.out.println();
        }
    }

    public static void printSquare(int size) {
        for (int i = 1; i <= size; i++) {
            for (int j = 1; j <= size; j++) {
                System.out.print("* ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter number of rows for triangle: ");
        int triangleRows = scanner.nextInt();
        if (triangleRows <= 0) {
            System.out.println("Number of rows must be positive.");
            scanner.close();
            return;
        }
        System.out.println("Triangle Pattern:");
        printTriangle(triangleRows);

        System.out.print("\nEnter size of square: ");
        int squareSize = scanner.nextInt();
        if (squareSize <= 0) {
            System.out.println("Size must be positive.");
            scanner.close();
            return;
        }
        System.out.println("Square Pattern:");
        printSquare(squareSize);

        scanner.close();
    }
}