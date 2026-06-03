package com.idanalyzer;

/** Thrown when an invalid client-side argument is supplied to the SDK. */
public class InvalidArgumentException extends RuntimeException {
    /**
     * Creates an exception describing the invalid argument.
     *
     * @param message the human-readable error message.
     */
    public InvalidArgumentException(String message) {
        super(message);
    }
}
