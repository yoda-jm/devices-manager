package com.jss.devicemanager.registration.exception;

import com.jss.devicemanager.common.exception.InvalidDeviceTypeException;
import com.jss.devicemanager.registration.model.RegisterDevice400Response;
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
        ResponseEntity<RegisterDevice400Response> response = handler.handleInvalidDeviceType(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_DEVICE_TYPE", response.getBody().getError());
        assertEquals("deviceType", response.getBody().getField());
        assertTrue(response.getBody().getMessage().contains("InvalidType"));
    }

    @Test
    void handleDuplicateDevice_shouldReturn409_withCorrectErrorResponse() {
        // Arrange
        DuplicateDeviceException exception = new DuplicateDeviceException("device123");

        // Act
        ResponseEntity<RegisterDevice400Response> response = handler.handleDuplicateDevice(exception);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("DUPLICATE_DEVICE_ID", response.getBody().getError());
        assertEquals("deviceID", response.getBody().getField());
        assertTrue(response.getBody().getMessage().contains("device123"));
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
        ResponseEntity<RegisterDevice400Response> response = handler.handleHttpMessageNotReadable(exception);

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
        ResponseEntity<RegisterDevice400Response> response = handler.handleHttpMessageNotReadable(exception);

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
        ResponseEntity<RegisterDevice400Response> response = handler.handleHttpMessageNotReadable(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_REQUEST", response.getBody().getError());
        assertEquals("Invalid request format", response.getBody().getMessage());
    }

    @Test
    void handleDuplicateDevice_shouldIncludeDeviceIdInMessage() {
        // Arrange
        String deviceId = "my-special-device-id-123";
        DuplicateDeviceException exception = new DuplicateDeviceException(deviceId);

        // Act
        ResponseEntity<RegisterDevice400Response> response = handler.handleDuplicateDevice(exception);

        // Assert
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains(deviceId));
        assertEquals("deviceID", response.getBody().getField());
    }

    @Test
    void handleInvalidDeviceType_shouldIncludeDeviceTypeInMessage() {
        // Arrange
        String invalidType = "PlayStation";
        InvalidDeviceTypeException exception = new InvalidDeviceTypeException(invalidType);

        // Act
        ResponseEntity<RegisterDevice400Response> response = handler.handleInvalidDeviceType(exception);

        // Assert
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains(invalidType));
        assertEquals("deviceType", response.getBody().getField());
    }
}
