package com.jss.devicemanager.registration.exception;

import com.jss.devicemanager.common.exception.InvalidDeviceTypeException;
import com.jss.devicemanager.registration.model.RegisterDevice400Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for returning proper error responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidDeviceTypeException.class)
    public ResponseEntity<RegisterDevice400Response> handleInvalidDeviceType(InvalidDeviceTypeException ex) {
        logger.debug("Invalid device type provided: {}", ex.getDeviceType());

        RegisterDevice400Response errorResponse = new RegisterDevice400Response();
        errorResponse.setError("INVALID_DEVICE_TYPE");
        errorResponse.setField("deviceType");
        errorResponse.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(DuplicateDeviceException.class)
    public ResponseEntity<RegisterDevice400Response> handleDuplicateDevice(DuplicateDeviceException ex) {
        logger.debug("Duplicate device ID: {}", ex.getDeviceId());

        RegisterDevice400Response errorResponse = new RegisterDevice400Response();
        errorResponse.setError("DUPLICATE_DEVICE_ID");
        errorResponse.setField("deviceID");
        errorResponse.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RegisterDevice400Response> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        logger.debug("Invalid JSON request: {}", ex.getMessage());

        RegisterDevice400Response errorResponse = new RegisterDevice400Response();

        // Check if it's an enum deserialization error
        if (ex.getMessage() != null && ex.getMessage().contains("DeviceTypeEnum")) {
            errorResponse.setError("INVALID_DEVICE_TYPE");
            errorResponse.setField("deviceType");
            errorResponse.setMessage("Invalid device type value provided");
        } else {
            errorResponse.setError("INVALID_REQUEST");
            errorResponse.setMessage("Invalid request format");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
