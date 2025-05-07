package ru.ifmo.lab6.server.exception;

/**
 * CannotDeleteFromDataBaseException - исключение, когда не удалось удалить элемент из коллекции в базе данных
 */
public class CannotDeleteFromDataBaseException extends RuntimeException {
    public CannotDeleteFromDataBaseException() {
        super();
    }

    public CannotDeleteFromDataBaseException(String message) {
        super(message);
    }
}
