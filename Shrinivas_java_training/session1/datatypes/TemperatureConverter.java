package Shrinivas_java_training.session1.datatypes;

import java.util.Scanner;

public class TemperatureConverter {

    // Method to convert Celsius to Fahrenheit
    public static float convertCelsiusToFahrenheit(float celsius) {
        // formula: F = C * 9/5 + 32
        return (celsius * 9 / 5) + 32;
    }

    // Method to convert Fahrenheit to Celsius
    public static float convertFahrenheitToCelsius(float fahrenheit) {
        // formula: C = (F - 32) * 5/9
        return (fahrenheit - 32) * 5 / 9;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose Option : 1. Celsius to Fahrenheit  2. Fahrenheit to Celsius");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                System.out.print("Enter temperature in Celsius: ");
                float celsius = scanner.nextFloat();
                float fahrenheit = convertCelsiusToFahrenheit(celsius);
                System.out.println(celsius + "°C = " + fahrenheit + "°F");
                break;

            case 2:
                System.out.print("Enter temperature in Fahrenheit: ");
                float fTemp = scanner.nextFloat();
                float cTemp = convertFahrenheitToCelsius(fTemp);
                System.out.println(fTemp + "°F = " + cTemp + "°C");
                break;

            default:
                System.out.println("Invalid choice!");
                break;
        }

        scanner.close();
    }
}