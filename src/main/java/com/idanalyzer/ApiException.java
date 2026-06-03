package com.idanalyzer;

/** Thrown when the ID Analyzer API returns an error, or a transport error occurs. */
public class ApiException extends RuntimeException {
    private final String code;

    public ApiException(String message, String code) {
        super(message);
        this.code = code;
    }

    public ApiException(String message, String code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /** The API error code (or "TRANSPORT" for network/transport errors). */
    public String getCode() {
        return code;
    }
}
