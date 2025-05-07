package ru.ifmo.lab6.server.exception;

public class CannotUploadCollectionException extends RuntimeException {
  public CannotUploadCollectionException() {
    super();
  }
  public CannotUploadCollectionException(String message) {
    super(message);
  }
}
