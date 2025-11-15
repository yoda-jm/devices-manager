package com.jss.devicemanager.common.exception;

/**
 * Exception thrown when an invalid device type is provided.
 */
public class InvalidDeviceTypeException extends RuntimeException {

    private final String deviceType;

    public InvalidDeviceTypeException(String deviceType) {
        super("Invalid device type: " + deviceType);
        this.deviceType = deviceType;
    }

    public String getDeviceType() {
        return deviceType;
    }
}
