package ru.ifmo.lab6.common.network;

import ru.ifmo.lab6.common.collectionObject.StudyGroup;

import java.io.Serializable;

public record Request(String command, String[] args, StudyGroup obj) implements Serializable {
}
