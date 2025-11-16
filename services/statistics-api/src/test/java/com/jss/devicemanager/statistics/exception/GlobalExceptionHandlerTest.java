package com.jss.devicemanager.statistics.exception;

import com.jss.devicemanager.common.exception.InvalidDeviceTypeException;
import com.jss.devicemanager.statistics.model.LogAuth400Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleInvalidDeviceType_shouldReturn400_withCorrectErrorResponse() {
        // Arrange
        InvalidDeviceTypeException exception = new InvalidDeviceTypeException("InvalidType");

        // Act
        ResponseEntity<LogAuth400Response> response = handler.handleInvalidDeviceType(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_DEVICE_TYPE", response.getBody().getError());
        assertEquals("deviceType", response.getBody().getField());
        assertTrue(response.getBody().getMessage().contains("InvalidType"));
    }

    @Test
    void handleHttpMessageNotReadable_shouldReturn400_forDeviceTypeEnumError() {
        // Arrange
        String errorMessage = "Cannot deserialize value of type DeviceTypeEnum from String \"UnknownType\"";
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException(
                errorMessage,
                (org.springframework.http.HttpInputMessage) null
        );

        // Act
        ResponseEntity<LogAuth400Response> response = handler.handleHttpMessageNotReadable(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_DEVICE_TYPE", response.getBody().getError());
        assertEquals("deviceType", response.getBody().getField());
        assertEquals("Invalid device type value provided", response.getBody().getMessage());
    }

    @Test
    void handleHttpMessageNotReadable_shouldReturn400_forGenericJsonError() {
        // Arrange
        String errorMessage = "Unexpected character (',' (code 44))";
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException(
                errorMessage,
                (org.springframework.http.HttpInputMessage) null
        );

        // Act
        ResponseEntity<LogAuth400Response> response = handler.handleHttpMessageNotReadable(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_REQUEST", response.getBody().getError());
        assertNull(response.getBody().getField());
        assertEquals("Invalid request format", response.getBody().getMessage());
    }

    @Test
    void handleHttpMessageNotReadable_shouldHandleNullMessage() {
        // Arrange
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException(
                null,
                (org.springframework.http.HttpInputMessage) null
        );

        // Act
        ResponseEntity<LogAuth400Response> response = handler.handleHttpMessageNotReadable(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_REQUEST", response.getBody().getError());
        assertEquals("Invalid request format", response.getBody().getMessage());
    }

    @Test
    void handleBadGateway_shouldReturn502_withCorrectErrorResponse() {
        // Arrange
        BadGatewayException exception = new BadGatewayException(
                "Device registration API is unavailable",
                new RuntimeException("Connection refused")
        );

        // Act
        ResponseEntity<LogAuth400Response> response = handler.handleBadGateway(exception);

        // Assert
        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("DEVICE_REGISTRATION_UNAVAILABLE", response.getBody().getError());
        assertEquals("Device registration API is unavailable", response.getBody().getMessage());
        assertNull(response.getBody().getField());
    }

    @Test
    void handleBadGateway_shouldHandleNullCause() {
        // Arrange
        BadGatewayException exception = new BadGatewayException(
                "Unexpected status code from device registration API",
                null
        );

        // Act
        ResponseEntity<LogAuth400Response> response = handler.handleBadGateway(exception);

        // Assert
        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("DEVICE_REGISTRATION_UNAVAILABLE", response.getBody().getError());
    }
}
