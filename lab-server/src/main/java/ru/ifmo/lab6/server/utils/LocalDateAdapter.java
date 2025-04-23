package ru.ifmo.lab6.server.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDate;

/**
 * The LocalDateAdapter class provides a custom serializer for LocalDate objects.
 * It converts LocalDate instances to JSON format using the Gson library.
 */
public class LocalDateAdapter implements JsonSerializer<LocalDate> {

    /**
     * Serializes a LocalDate object to a JSON element.
     *
     * @param date the LocalDate object to be serialized.
     * @param typeOfSrc the type of the source object (LocalDate).
     * @param context the context of the serialization process.
     * @return a JsonPrimitive containing the string representation of the LocalDate.
     */
    @Override
    public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(date.toString());
    }
}
