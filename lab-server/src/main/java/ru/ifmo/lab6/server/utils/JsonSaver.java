package ru.ifmo.lab6.server.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.ifmo.lab6.common.collectionObject.StudyGroup;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * The JsonSaver class provides functionality to save a list of StudyGroup objects to a JSON file.
 */
public class JsonSaver {

    /**
     * Saves the list of StudyGroup objects to a JSON file.
     *
     * @param studyGroups the list of StudyGroup objects to be saved.
     * @throws IOException if an I/O error occurs while writing to the file.
     */
    public static void saveFile(ArrayList<StudyGroup> studyGroups) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
        String json = gson.toJson(studyGroups);

        String path = System.getenv("FILENAME");
        if (path == null) {
            System.out.println("Переменная окружения не задана!");
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(path);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
        ) {
            bos.write(json.getBytes());
        } catch (IOException e) {
            System.out.println("Не удалось сохранить файл!");
        }
    }
}
