package Shrinivas_java_training.session1.strings;

import java.util.Scanner;

public class VowelCounter {

    // count number of vowels in a string
    public static int countVowels(String word) {
        int count = 0;

        for (int i = 0; i < word.length(); i++) {
            char ch = Character.toLowerCase(word.charAt(i));

            if (ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u') {
                count++;
            }
        }

        return count;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter a word: ");
        String word = scanner.next();

        int vowelCount = countVowels(word);

        System.out.println("Number of vowels: " + vowelCount);

        scanner.close();
    }
}