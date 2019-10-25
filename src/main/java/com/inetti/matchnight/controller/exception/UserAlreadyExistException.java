package com.inetti.matchnight.controller.exception;

public class UserAlreadyExistException extends RuntimeException{

    private static final String MESSAGE_FORMAT = "user with username '%s' already exists";

    public UserAlreadyExistException(String username) {
        super(String.format(MESSAGE_FORMAT, username));
    }
}
