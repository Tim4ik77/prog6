package ru.ifmo.lab6.server.utils;

import com.google.gson.*;
import ru.ifmo.lab6.common.collectionObject.*;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * The ProductLoader class is responsible for loading product data from a JSON file.
 * It parses the JSON content and converts it into a list of Product objects.
 */
public class JsonLoader {
    /**
     * A list to store the products loaded from the JSON file.
     */
    private ArrayList<StudyGroup> studyGroups = new ArrayList<>();

    /**
     * A set to track unique IDs to avoid duplicates.
     */
    private Set<Integer> uniqueIds = new HashSet<>();

    /**
     * Loads products from a JSON file located at the specified file path.
     *
     * @param varName The path to the JSON file containing product data.
     */
    public ArrayList<StudyGroup> loadFile(String varName) {
        String path = System.getenv(varName);
        if (path == null) {
            System.out.println("Переменная окружения не задана!");
            return studyGroups;
        }
        try (FileReader reader = new FileReader(path)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);

            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();

                for (JsonElement element : jsonArray) {
                    if (element.isJsonObject()) {
                        JsonObject jsonObject = element.getAsJsonObject();

                        Integer id = getIntegerField(jsonObject, "id");
                        if (id == null || id < 0) {
                            System.out.println("Поле 'id' отсутствует, некорректно или не является положительным числом.");
                            continue;
                        }

                        if (uniqueIds.contains(id)) {
                            System.out.println("Обнаружен дубликат ID: " + id + ". Группа пропущена.");
                            continue;
                        }
                        uniqueIds.add(id);

                        String name = getStringField(jsonObject, "name");
                        if (name == null) {
                            System.out.println("Поле 'name' отсутствует или некорректно.");
                            continue;
                        }

                        JsonElement coordinatesElement = jsonObject.get("coordinates");
                        if (coordinatesElement == null || coordinatesElement.isJsonNull()) {
                            System.out.println("Координаты отсутствуют.");
                            continue;
                        }
                        JsonObject coordinatesJson = coordinatesElement.getAsJsonObject();

                        Float x = getFloatField(coordinatesJson, "x");
                        Long y = getLongField(coordinatesJson, "y");
                        if (x == null || y == null) {
                            System.out.println("Координаты 'x' или 'y' отсутствуют или некорректны.");
                            continue;
                        }
                        Coordinates coordinates = new Coordinates(x, y);

                        LocalDate creationDate = getCreationDateField(jsonObject, "creationDate");

                        Long studentsCount = getLongField(jsonObject, "studentsCount");
                        if (studentsCount == null || studentsCount <= 0) {
                            System.out.println("Поле 'studentsCount' отсутствует или некорректно.");
                            continue;
                        }

                        Integer shouldBeExpelled = getIntegerField(jsonObject, "shouldBeExpelled");
                        if (shouldBeExpelled == null || shouldBeExpelled <= 0) {
                            System.out.println("Поле 'shouldBeExpelled' отсутствует или некорректно.");
                            continue;
                        }

                        Long transferredStudents = getLongField(jsonObject, "transferredStudents");
                        if (transferredStudents == null || transferredStudents <= 0) {
                            System.out.println("Поле 'transferredStudents' отсутствует или некорректно.");
                            continue;
                        }

                        String formOfEducationString = getStringField(jsonObject, "formOfEducation");
                        FormOfEducation formOfEducation = null;

                        if (formOfEducationString != null && !formOfEducationString.isEmpty()) {
                            try {
                                formOfEducation = FormOfEducation.valueOf(formOfEducationString);
                            } catch (IllegalArgumentException e) {
                                System.out.println("Некорректное значение для 'formOfEducation': " + formOfEducationString);
                                continue;
                            }
                        }

                        Person groupAdmin = null;
                        JsonElement groupAdminElement = jsonObject.get("groupAdmin");
                        if (groupAdminElement != null && !groupAdminElement.isJsonNull()) {
                            JsonObject groupAdminJson = groupAdminElement.getAsJsonObject();

                            String adminName = getStringField(groupAdminJson, "name");
                            Integer weight = getIntegerField(groupAdminJson, "weight");
                            String eyeColorString = getStringField(groupAdminJson, "eyeColor");
                            String hairColorString = getStringField(groupAdminJson, "hairColor");
                            String nationalityString = getStringField(groupAdminJson, "nationality");

                            if (adminName == null || weight == null || eyeColorString == null || hairColorString == null) {
                                System.out.println("Некорректные данные для 'groupAdmin'.");
                                continue;
                            }

                            Color eyeColor = Color.valueOf(eyeColorString);
                            Color hairColor = Color.valueOf(hairColorString);
                            Country nationality = nationalityString != null ? Country.valueOf(nationalityString) : null;

                            groupAdmin = new Person(adminName, weight, eyeColor, hairColor, nationality);
                        }

                        StudyGroup studyGroup = new StudyGroup(id, name, coordinates, creationDate, studentsCount, shouldBeExpelled, transferredStudents, formOfEducation, groupAdmin);
                        studyGroups.add(studyGroup);
                    }
                }
            } else {
                System.out.println("JSON-файл не является массивом.");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке данных из файла: " + e.getMessage());
        }
        return studyGroups;
    }

    /**
     * Helper method to safely get a String field from a JsonObject.
     *
     * @param jsonObject The JsonObject to extract the field from.
     * @param fieldName  The name of the field.
     * @return The value of the field, or null if the field is missing or invalid.
     */
    private String getStringField(JsonObject jsonObject, String fieldName) {
        if (jsonObject.has(fieldName)) {
            try {
                return jsonObject.get(fieldName).getAsString();
            } catch (UnsupportedOperationException | IllegalStateException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Helper method to safely get a Double field from a JsonObject.
     *
     * @param jsonObject The JsonObject to extract the field from.
     * @param fieldName  The name of the field.
     * @return The value of the field, or null if the field is missing or invalid.
     */
    private Float getFloatField(JsonObject jsonObject, String fieldName) {
        if (jsonObject.has(fieldName)) {
            try {
                return jsonObject.get(fieldName).getAsFloat();
            } catch (UnsupportedOperationException | IllegalStateException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Helper method to safely get an Integer field from a JsonObject.
     *
     * @param jsonObject The JsonObject to extract the field from.
     * @param fieldName  The name of the field.
     * @return The value of the field, or null if the field is missing or invalid.
     */
    private Integer getIntegerField(JsonObject jsonObject, String fieldName) {
        if (jsonObject.has(fieldName)) {
            try {
                return jsonObject.get(fieldName).getAsInt();
            } catch (UnsupportedOperationException | IllegalStateException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Helper method to safely get a Long field from a JsonObject.
     *
     * @param jsonObject The JsonObject to extract the field from.
     * @param fieldName  The name of the field.
     * @return The value of the field, or null if the field is missing or invalid.
     */
    private Long getLongField(JsonObject jsonObject, String fieldName) {
        if (jsonObject.has(fieldName)) {
            try {
                return jsonObject.get(fieldName).getAsLong();
            } catch (UnsupportedOperationException | IllegalStateException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Helper method to safely get a LocalDate field from a JsonObject.
     *
     * @param jsonObject The JsonObject to extract the field from.
     * @param fieldName  The name of the field.
     * @return The value of the field, or null if the field is missing or invalid.
     */
    private LocalDate getCreationDateField(JsonObject jsonObject, String fieldName) {
        if (jsonObject.has(fieldName)) {
            try {
                String dateString = jsonObject.get(fieldName).getAsString();
                return LocalDate.parse(dateString);
            } catch (UnsupportedOperationException
                     | IllegalStateException
                     | DateTimeParseException e) {
                return null;
            }
        }
        return null;
    }
}