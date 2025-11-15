package com.jss.devicemanager.statistics.exception;

import com.jss.devicemanager.common.exception.InvalidDeviceTypeException;
import com.jss.devicemanager.statistics.model.LogAuth400Response;
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
    public ResponseEntity<LogAuth400Response> handleInvalidDeviceType(InvalidDeviceTypeException ex) {
        logger.debug("Invalid device type provided: {}", ex.getDeviceType());

        LogAuth400Response errorResponse = new LogAuth400Response();
        errorResponse.setError("INVALID_DEVICE_TYPE");
        errorResponse.setField("deviceType");
        errorResponse.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<LogAuth400Response> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        logger.debug("Invalid JSON request: {}", ex.getMessage());

        LogAuth400Response errorResponse = new LogAuth400Response();

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

    @ExceptionHandler(BadGatewayException.class)
    public ResponseEntity<LogAuth400Response> handleBadGateway(BadGatewayException ex) {
        logger.error("Device registration API unavailable: {}", ex.getMessage());

        LogAuth400Response errorResponse = new LogAuth400Response();
        errorResponse.setError("DEVICE_REGISTRATION_UNAVAILABLE");
        errorResponse.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorResponse);
    }
}
