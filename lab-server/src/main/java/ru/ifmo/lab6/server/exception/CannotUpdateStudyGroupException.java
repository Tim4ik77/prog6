package ru.ifmo.lab6.server.exception;

/**
 * CannotUpdateMusicBandException - исключение, когда не удалось обновить элемент в коллекции в базе данных
 */
public class CannotUpdateStudyGroupException extends RuntimeException {
    public CannotUpdateStudyGroupException() { super();}
    public CannotUpdateStudyGroupException(String message) {
        super(message);
    }
}
