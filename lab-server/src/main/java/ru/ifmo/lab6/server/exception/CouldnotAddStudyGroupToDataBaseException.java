package ru.ifmo.lab6.server.exception;

public class CouldnotAddStudyGroupToDataBaseException extends RuntimeException {
    public CouldnotAddStudyGroupToDataBaseException() {
        super();
    }
    public CouldnotAddStudyGroupToDataBaseException(String message) {
        super(message);
    }
}
