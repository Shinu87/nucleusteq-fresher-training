package Shrinivas_java_training.session1.generics;

import java.util.Scanner;

public class AreaCalculator {

    // Method to calculate area of a circle
    public static float getCircleArea(float radius, float pi) {
        return pi * radius * radius;
    }

    // Method to calculate area of a rectangle
    public static float getRectangleArea(float height, float width) {
        return height * width;
    }

    // Method to calculate area of a triangle
    public static float getTriangleArea(float base, float height) {
        return 0.5f * base * height;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter shape (circle, rectangle, or triangle):");
        String shape = scanner.nextLine();

        if (shape.equalsIgnoreCase("circle")) {
            System.out.println("Enter radius of the circle:");
            float radius = scanner.nextFloat();
            float pi = 3.141516f;
            float area = getCircleArea(radius, pi);
            System.out.println("Area of the circle is " + area);

        } else if (shape.equalsIgnoreCase("rectangle")) {
            System.out.println("Enter height of the rectangle:");
            float height = scanner.nextFloat();
            System.out.println("Enter width of the rectangle:");
            float width = scanner.nextFloat();
            float area = getRectangleArea(height, width);
            System.out.println("Area of the rectangle is " + area);

        } else if (shape.equalsIgnoreCase("triangle")) {
            System.out.println("Enter base of the triangle:");
            float base = scanner.nextFloat();
            System.out.println("Enter height of the triangle:");
            float triHeight = scanner.nextFloat();
            float area = getTriangleArea(base, triHeight);
            System.out.println("Area of the triangle is " + area);

        } else {
            System.out.println("Shape not found! Enter either circle, rectangle, or triangle.");
        }
        scanner.close();
    }
}