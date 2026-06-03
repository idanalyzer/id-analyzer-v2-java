package com.idanalyzer;

/** Thrown when the ID Analyzer API returns an error, or a transport error occurs. */
public class ApiException extends RuntimeException {
    /** The API error code, or a transport code such as "TRANSPORT". */
    private final String code;

    /**
     * Creates an exception with an error message and code.
     *
     * @param message the human-readable error message.
     * @param code the API error code, or a transport code such as "TRANSPORT".
     */
    public ApiException(String message, String code) {
        super(message);
        this.code = code;
    }

    /**
     * Creates an exception with an error message, code and underlying cause.
     *
     * @param message the human-readable error message.
     * @param code the API error code, or a transport code such as "TRANSPORT".
     * @param cause the underlying throwable that triggered this exception.
     */
    public ApiException(String message, String code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * The API error code (or "TRANSPORT" for network/transport errors).
     *
     * @return the error code associated with this exception.
     */
    public String getCode() {
        return code;
    }
}
