package ru.ifmo.lab6.server.exception;

/**
 * UserIsNotAuthorizedException - исключение, когда пользователь не авторизирован
 */
public class UserIsNotAuthorizedException extends RuntimeException {
    public UserIsNotAuthorizedException() {
        super();
    }
}
