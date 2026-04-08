package Shrinivas_java_training.session1.array;

import java.util.Scanner;

public class ArraySearch {

    // linear search to check if number exists in array
    public static boolean searchNumberInArray(int[] array, int num) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == num) {
                return true;
            }
        }
        return false;
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

        System.out.print("Enter number to search: ");
        int number = scanner.nextInt();

        boolean found = searchNumberInArray(array, number);

        if (found) {
            System.out.println("Number found in array");
        } else {
            System.out.println("Number not found in array");
        }

        scanner.close();
    }
}