package Shrinivas_java_training.session1.generics;

import java.util.Scanner;

public class AreaCalculator {

    public static float getCircleArea(float radius, float pi) {
        return pi * radius * radius;
    }

    public static float getRectangleArea(float height, float width) {
        return height * width;
    }

    public static float getTriangleArea(float base, float height) {
        return 0.5f * base * height;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter shape (circle, rectangle, or triangle):");
        String shape = scanner.nextLine();

        if (shape.equalsIgnoreCase("circle")) {
            System.out.println("Enter radius of the circle:");

            if (!scanner.hasNextFloat()) {
                System.out.println("Enter a number.");
                scanner.close();
                return;
            }
            float radius = scanner.nextFloat();

            if (radius <= 0) {
                System.out.println("Radius must be positive");
                scanner.close();
                return;
            }

            float pi = 3.141516f;
            float area = getCircleArea(radius, pi);
            System.out.println("Area of the circle is " + area);

        } else if (shape.equalsIgnoreCase("rectangle")) {
            System.out.println("Enter height of the rectangle:");

            if (!scanner.hasNextFloat()) {
                System.out.println("Enter a number.");
                scanner.close();
                return;
            }
            float height = scanner.nextFloat();

            System.out.println("Enter width of the rectangle:");

            if (!scanner.hasNextFloat()) {
                System.out.println("Enter a number.");
                scanner.close();
                return;
            }
            float width = scanner.nextFloat();

            if (height <= 0 || width <= 0) {
                System.out.println("Height and width must be positive");
                scanner.close();
                return;
            }

            float area = getRectangleArea(height, width);
            System.out.println("Area of the rectangle is " + area);

        } else if (shape.equalsIgnoreCase("triangle")) {
            System.out.println("Enter base of the triangle:");

            if (!scanner.hasNextFloat()) {
                System.out.println("Enter a number.");
                scanner.close();
                return;
            }
            float base = scanner.nextFloat();

            System.out.println("Enter height of the triangle:");

            if (!scanner.hasNextFloat()) {
                System.out.println("Enter a number.");
                scanner.close();
                return;
            }
            float triHeight = scanner.nextFloat();

            if (base <= 0 || triHeight <= 0) {
                System.out.println("Base and height must be positive");
                scanner.close();
                return;
            }

            float area = getTriangleArea(base, triHeight);
            System.out.println("Area of the triangle is " + area);

        } else {
            System.out.println("Shape not found!");
        }

        scanner.close();
    }
}