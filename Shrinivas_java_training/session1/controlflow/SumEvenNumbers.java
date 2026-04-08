package Shrinivas_java_training.session1.controlflow;

public class SumEvenNumbers {

    // calculate sum of even numbers from 1 to 10
    public static int getSumEven() {
        int sum = 0;
        int i = 1;

        while (i <= 10) {
            if (i % 2 == 0) {
                sum = sum + i;
            }
            i++;
        }

        return sum;
    }

    public static void main(String[] args) {
        int result = getSumEven();
        System.out.println("Sum of even numbers from 1 to 10 is: " + result);
    }
}