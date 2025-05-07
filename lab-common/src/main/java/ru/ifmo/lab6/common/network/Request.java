package ru.ifmo.lab6.common.network;

import ru.ifmo.lab6.common.collectionObject.StudyGroup;

import java.io.Serializable;

public record Request(String command, String[] args, StudyGroup obj, User user) implements Serializable {
    public Request(String command, String[] args, StudyGroup obj) {
        this(command, args, obj, null);
    }
}
