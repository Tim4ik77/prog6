package ru.ifmo.lab6.common.network;


import ru.ifmo.lab6.common.collectionObject.StudyGroup;

import java.io.Serializable;
import java.util.ArrayList;

public record Response(String message, ArrayList<StudyGroup> studyGroups) implements Serializable {
    public Response(String message) {
        this(message, null);
    }
}
