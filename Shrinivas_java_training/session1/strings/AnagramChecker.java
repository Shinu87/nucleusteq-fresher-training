// using hashmap to count character frequency
package Shrinivas_java_training.session1.strings;

import java.util.Scanner;
import java.util.HashMap;

public class AnagramChecker {

    // check if two strings are anagrams
    public static boolean checkAnagram(String word1, String word2) {

        if (word1.length() != word2.length()) {
            return false;
        }

        HashMap<Character, Integer> map = new HashMap<>();

        // count characters of first word
        for (int i = 0; i < word1.length(); i++) {
            char ch = word1.charAt(i);

            if (map.containsKey(ch)) {
                map.put(ch, map.get(ch) + 1);
            } else {
                map.put(ch, 1);
            }
        }

        // decrease count using second word
        for (int i = 0; i < word2.length(); i++) {
            char ch = word2.charAt(i);

            if (!map.containsKey(ch) || map.get(ch) == 0) {
                return false;
            }

            map.put(ch, map.get(ch) - 1);
        }

        return true;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter first word: ");
        String word1 = scanner.next();

        System.out.print("Enter second word: ");
        String word2 = scanner.next();

        boolean result = checkAnagram(word1.toLowerCase(), word2.toLowerCase());

        if (result) {
            System.out.println("Strings are anagrams");
        } else {
            System.out.println("Strings are not anagrams");
        }

        scanner.close();
    }
}