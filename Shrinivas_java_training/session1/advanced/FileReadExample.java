// reading data from text file using Scanner
package Shrinivas_java_training.session1.advanced;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileReadExample {

    public static void main(String[] args) {

        try {
            File file = new File("Shrinivas_java_training\\session1\\advanced\\sample.txt"); // file in same folder
            Scanner reader = new Scanner(file);

            // read file line by line
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                System.out.println(line);
            }

            reader.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }
}