package com.jss.devicemanager.statistics.exception;

/**
 * Exception thrown when the device registration API is unavailable or fails.
 */
public class BadGatewayException extends RuntimeException {

    public BadGatewayException(String message, Throwable cause) {
        super(message, cause);
    }
}
