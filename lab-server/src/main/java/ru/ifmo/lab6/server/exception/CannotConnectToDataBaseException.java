package ru.ifmo.lab6.server.exception;

/**
 * CannotConnectToDataBaseException - исключение, когда не удалось подключиться или отправить запрос к базе данных
 */
public class CannotConnectToDataBaseException extends RuntimeException {
    public CannotConnectToDataBaseException() {super();}
    public CannotConnectToDataBaseException(String message) {
        super(message);
    }
}
