package com.idanalyzer;

/** Thrown when an invalid client-side argument is supplied to the SDK. */
public class InvalidArgumentException extends RuntimeException {
    public InvalidArgumentException(String message) {
        super(message);
    }
}
