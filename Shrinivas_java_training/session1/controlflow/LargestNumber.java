package Shrinivas_java_training.session1.controlflow;

import java.util.Scanner;

public class LargestNumber {

    public static int checkLargestNumber(int a, int b, int c) {
        if (a >= b && a >= c) {
            return a;
        } else if (b >= a && b >= c) {
            return b;
        } else {
            return c;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter three numbers: ");
        int a = sc.nextInt();
        int b = sc.nextInt();
        int c = sc.nextInt();

        int largestNumber = checkLargestNumber(a, b, c);

        System.out.println("Largest number is: " + largestNumber);

        sc.close();
    }
}