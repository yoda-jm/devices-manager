package com.jss.devicemanager.registration.exception;

/**
 * Exception thrown when attempting to register a device with an already existing deviceID.
 */
public class DuplicateDeviceException extends RuntimeException {

    private final String deviceId;

    public DuplicateDeviceException(String deviceId) {
        super("Device with ID already exists: " + deviceId);
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
