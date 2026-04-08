package Shrinivas_java_training.session1.array;

import java.util.Scanner;

public class ArrayAverage {

    // calculate average of array elements
    public static double getAverage(int[] array) {
        int totalSum = 0;

        for (int i = 0; i < array.length; i++) {
            totalSum += array[i];
        }

        double average = totalSum / array.length;

        return average;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter size of array: ");
        int size = scanner.nextInt();
        if (size <= 0) {
            System.out.println("Size should be greater than 0");
            scanner.close();
            return;
        }
        int[] array = new int[size];

        System.out.println("Enter elements:");
        for (int i = 0; i < size; i++) {
            array[i] = scanner.nextInt();
        }

        double average = getAverage(array);

        System.out.println("Average is: " + average);

        scanner.close();
    }
}