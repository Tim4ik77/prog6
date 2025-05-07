package ru.ifmo.lab6.common.network;

import ru.ifmo.lab6.common.collectionObject.StudyGroup;

import java.io.Serializable;
import java.util.ArrayList;

public record Response(String message, ArrayList<StudyGroup> studyGroups, boolean success) implements Serializable {
    public Response(String message) {
        this(message, null, true);
    }

    public Response(String message, boolean success) {
        this(message, null, success);
    }

    public Response(String message, ArrayList<StudyGroup> studyGroups) {
        this(message, studyGroups, true);
    }
}
