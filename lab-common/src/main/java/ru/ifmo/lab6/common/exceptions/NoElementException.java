package ru.ifmo.lab6.common.exceptions;

/**
 * The NoElementException class is a custom exception that is thrown when an operation is performed
 * on a collection element that does not exist.
 */
public class NoElementException extends RuntimeException {

    /**
     * Constructs a NoElementException with the specified detail message.
     *
     * @param message the detail message.
     */
    public NoElementException(String message) {
        super(message);
    }
}
