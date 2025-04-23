package ru.ifmo.lab6.client.utils;

import ru.ifmo.lab6.common.collectionObject.*;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * The Parser class provides functionality to parse user input and create StudyGroup objects.
 * It uses a Scanner to read input from the user and constructs StudyGroup objects based on the input.
 */
public class Parser {

    private final Scanner sc;

    /**
     * Constructs a Parser object with the given Scanner.
     *
     * @param sc the Scanner object used to read user input.
     */
    public Parser(Scanner sc) {
        this.sc = sc;
    }

    /**
     * Parses user input to create a StudyGroup object.
     *
     * @return a StudyGroup object created from user input.
     */
    public StudyGroup parseStudyGroup() {
        System.out.println("Введите имя группы: ");
        String groupName = ScanUtil.parseNonEmptyString(sc);

        System.out.println("Введите количество студентов в группе: ");
        long studentsCount = ScanUtil.parsePositiveLong(sc);

        System.out.println("Введите количество студентов к отчислению: ");
        int shouldBeExpelled = ScanUtil.parsePositiveInt(sc);

        System.out.println("Введите количество переведенных студентов: ");
        long transferredStudents = ScanUtil.parsePositiveLong(sc);

        System.out.println("Введите форму обучения!");
        FormOfEducation formOfEducation = ScanUtil.parseEnum(sc, FormOfEducation.class, true);

        Coordinates coordinates = parseCoordinates();
        Person groupAdmin = parseAdmin();

        LocalDate creationDate = LocalDate.now();

        return new StudyGroup(-1, groupName, coordinates, creationDate, studentsCount,
                shouldBeExpelled, transferredStudents, formOfEducation, groupAdmin);
    }

    /**
     * Parses user input to create a Coordinates object.
     *
     * @return a Coordinates object created from user input.
     */
    private Coordinates parseCoordinates() {
        System.out.println("Введите координату x: ");
        float x = ScanUtil.parseFloat(sc);

        System.out.println("Введите координату y: ");
        Long y = ScanUtil.parseLong(sc);

        return new Coordinates(x, y);
    }

    /**
     * Parses user input to create a Person object representing the group admin.
     *
     * @return a Person object created from user input, or null if the group has no admin.
     */
    private Person parseAdmin() {
        System.out.println("Есть ли у группы староста (y/n)? ");
        while (true) {
            String input = sc.nextLine();
            if (input.equalsIgnoreCase("y")) {
                System.out.println("Введите имя человека: ");
                String adminName = ScanUtil.parseNonEmptyString(sc);

                System.out.println("Введите вес человека: ");
                Integer weight = ScanUtil.parsePositiveInt(sc);

                System.out.println("Введите цвет глаз!");
                Color eyeColor = ScanUtil.parseEnum(sc, Color.class, false);
                System.out.println("Введите цвет волос!");
                Color hairColor = ScanUtil.parseEnum(sc, Color.class, false);

                System.out.println("Введите страну рождения!");
                Country nationality = ScanUtil.parseEnum(sc, Country.class, false);
                return new Person(adminName, weight, eyeColor, hairColor, nationality);
            } else if (input.equalsIgnoreCase("n")) {
                return null;
            } else {
                System.out.println("Неправильный формат ввода! Введите y(es)/n(o)!");
            }
        }
    }
}
