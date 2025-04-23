package ru.ifmo.lab6.client.utils;

import java.util.Arrays;
import java.util.Scanner;

/**
 * The ScanUtil class provides utility methods for parsing user input from a Scanner.
 * It includes methods for parsing integers, longs, floats, positive numbers, non-empty strings,
 * and enumerations.
 */
public class ScanUtil {

    /**
     * Parses a long from the user input.
     *
     * @param scanner the Scanner object used to read user input.
     * @return the parsed long value.
     */
    public static long parseLong(Scanner scanner) {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Введите число в формате long!");
            }
        }
    }

    /**
     * Parses a float from the user input.
     *
     * @param scanner the Scanner object used to read user input.
     * @return the parsed float value.
     */
    public static float parseFloat(Scanner scanner) {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Float.parseFloat(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Введите число в формате float!");
            }
        }
    }

    /**
     * Parses a positive long from the user input.
     *
     * @param scanner the Scanner object used to read user input.
     * @return the parsed positive long value.
     */
    public static long parsePositiveLong(Scanner scanner) {
        while (true) {
            try {
                long input = Long.parseLong(scanner.nextLine());
                if (input <= 0) {
                    System.out.println("Значение должно быть больше нуля!");
                } else {
                    return input;
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Введите число в формате long!");
            }
        }
    }

    /**
     * Parses a positive integer from the user input.
     *
     * @param scanner the Scanner object used to read user input.
     * @return the parsed positive integer value.
     */
    public static int parsePositiveInt(Scanner scanner) {
        while (true) {
            try {
                int input = Integer.parseInt(scanner.nextLine());
                if (input <= 0) {
                    System.out.println("Значение должно быть больше нуля!");
                } else {
                    return input;
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Введите число в формате long!");
            }
        }
    }

    /**
     * Parses a non-empty string from the user input.
     *
     * @param scanner the Scanner object used to read user input.
     * @return the parsed non-empty string value.
     */
    public static String parseNonEmptyString(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                System.out.println("Длина строки должна быть больше нуля!");
            } else {
                return input;
            }
        }
    }

    /**
     * Parses an enumeration value from the user input.
     *
     * @param scanner the Scanner object used to read user input.
     * @param enumClass the Class object of the enumeration type.
     * @param allowEmpty whether an empty input is allowed.
     * @param <T> the type of the enumeration.
     * @return the parsed enumeration value, or null if allowEmpty is true and the input is empty.
     */
    public static <T extends Enum<T>> T parseEnum(Scanner scanner, Class<T> enumClass, boolean allowEmpty) {
        while (true) {
            String message = allowEmpty
                    ? "Введите одно из значений или пустое поле, если хотите пропустить: "
                    : "Введите одно из значений: ";
            System.out.println(message + Arrays.toString(enumClass.getEnumConstants()));

            String input = scanner.nextLine();
            if (allowEmpty && input.isEmpty()) {
                return null;
            }

            try {
                return Enum.valueOf(enumClass, input);
            } catch (IllegalArgumentException e) {
                System.out.println("Вы ввели неправильный аргумент!");
            }
        }
    }
}
