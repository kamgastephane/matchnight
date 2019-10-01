package com.inetti.matchnight.configuration.exception;

/**
 * Custom exception class used when invalid argument are passed to controllers
 */
public class InvalidInputArgumentException extends IllegalArgumentException {

    private static final String PARAMETER_MESSAGE = "parameter %s has invalid value";
    private static final String MESSAGE = PARAMETER_MESSAGE + "\nmessage: %s";


    public InvalidInputArgumentException(String parameter) {
        super(String.format(PARAMETER_MESSAGE, parameter));
    }

    public InvalidInputArgumentException(String parameter, String message) {
        super(String.format(MESSAGE, parameter, message));
    }

    public InvalidInputArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

}
