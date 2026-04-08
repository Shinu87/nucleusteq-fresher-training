// Strings are immutable so I build a new reversed string
package Shrinivas_java_training.session1.strings;

import java.util.Scanner;

public class StringReverse {

    // reverse string using loop
    public static String reverseWord(String word) {
        String reversedWord = "";

        for (int i = word.length() - 1; i >= 0; i--) {
            reversedWord += word.charAt(i);
        }

        return reversedWord;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter a word: ");
        String word = scanner.next();

        String reversedWord = reverseWord(word);

        System.out.println("Reversed word: " + reversedWord);

        scanner.close();
    }
}