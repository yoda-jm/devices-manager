package com.jss.devicemanager.statistics.service;

import com.jss.devicemanager.statistics.model.AuthRequest;
import com.jss.devicemanager.statistics.service.DeviceRegistrationClient.DeviceRegistrationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceRegistrationClientTest {

    @Mock
    private RestTemplate restTemplate;

    private DeviceRegistrationClient client;

    private final String apiUrl = "http://localhost:8080";
    private final String apiKey = "test-api-key";

    @BeforeEach
    void setUp() {
        client = new DeviceRegistrationClient(restTemplate, apiUrl, apiKey);
    }

    @Test
    void registerDevice_shouldReturnStatusCode_whenRegistrationSucceeds() throws DeviceRegistrationException {
        // Arrange
        AuthRequest authRequest = new AuthRequest();
        authRequest.setDeviceID("device123");
        authRequest.setDeviceType(AuthRequest.DeviceTypeEnum.I_OS);

        ResponseEntity<String> mockResponse = ResponseEntity.ok("Success");
        when(restTemplate.exchange(
                eq(apiUrl + "/Device/register"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(mockResponse);

        // Act
        int statusCode = client.registerDevice(authRequest);

        // Assert
        assertEquals(200, statusCode);

        // Verify request headers and body
        ArgumentCaptor<HttpEntity<Map<String, String>>> captor =
                ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                eq(apiUrl + "/Device/register"),
                eq(HttpMethod.POST),
                captor.capture(),
                eq(String.class)
        );

        HttpEntity<Map<String, String>> capturedRequest = captor.getValue();
        assertEquals("test-api-key", capturedRequest.getHeaders().get("X-Device-Registration-API-Key").get(0));
        assertEquals(MediaType.APPLICATION_JSON, capturedRequest.getHeaders().getContentType());
        assertEquals("device123", capturedRequest.getBody().get("deviceID"));
        assertEquals("iOS", capturedRequest.getBody().get("deviceType"));
    }

    @Test
    void registerDevice_shouldReturn409_whenDeviceAlreadyExists() throws DeviceRegistrationException {
        // Arrange
        AuthRequest authRequest = new AuthRequest();
        authRequest.setDeviceID("device123");
        authRequest.setDeviceType(AuthRequest.DeviceTypeEnum.ANDROID);

        when(restTemplate.exchange(
                any(String.class),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.CONFLICT));

        // Act
        int statusCode = client.registerDevice(authRequest);

        // Assert
        assertEquals(409, statusCode);
    }

    @Test
    void registerDevice_shouldReturn400_whenBadRequest() throws DeviceRegistrationException {
        // Arrange
        AuthRequest authRequest = new AuthRequest();
        authRequest.setDeviceID("device123");
        authRequest.setDeviceType(AuthRequest.DeviceTypeEnum.WATCH);

        when(restTemplate.exchange(
                any(String.class),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // Act
        int statusCode = client.registerDevice(authRequest);

        // Assert
        assertEquals(400, statusCode);
    }

    @Test
    void registerDevice_shouldThrowException_whenServerError() {
        // Arrange
        AuthRequest authRequest = new AuthRequest();
        authRequest.setDeviceID("device123");
        authRequest.setDeviceType(AuthRequest.DeviceTypeEnum.TV);

        when(restTemplate.exchange(
                any(String.class),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // Act & Assert
        DeviceRegistrationException exception = assertThrows(
                DeviceRegistrationException.class,
                () -> client.registerDevice(authRequest)
        );

        assertTrue(exception.getMessage().contains("Device registration API returned error"));
        assertTrue(exception.getMessage().contains("500"));
        assertInstanceOf(HttpServerErrorException.class, exception.getCause());
    }

    @Test
    void registerDevice_shouldThrowException_whenServiceUnavailable() {
        // Arrange
        AuthRequest authRequest = new AuthRequest();
        authRequest.setDeviceID("device123");
        authRequest.setDeviceType(AuthRequest.DeviceTypeEnum.I_OS);

        when(restTemplate.exchange(
                any(String.class),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE));

        // Act & Assert
        DeviceRegistrationException exception = assertThrows(
                DeviceRegistrationException.class,
                () -> client.registerDevice(authRequest)
        );

        assertTrue(exception.getMessage().contains("Device registration API returned error"));
        assertTrue(exception.getMessage().contains("503"));
    }

    @Test
    void registerDevice_shouldThrowException_whenConnectionFails() {
        // Arrange
        AuthRequest authRequest = new AuthRequest();
        authRequest.setDeviceID("device123");
        authRequest.setDeviceType(AuthRequest.DeviceTypeEnum.I_OS);

        when(restTemplate.exchange(
                any(String.class),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new ResourceAccessException("Connection refused"));

        // Act & Assert
        DeviceRegistrationException exception = assertThrows(
                DeviceRegistrationException.class,
                () -> client.registerDevice(authRequest)
        );

        assertEquals("Device registration API is unavailable", exception.getMessage());
        assertInstanceOf(ResourceAccessException.class, exception.getCause());
    }

    @Test
    void registerDevice_shouldThrowException_whenUnexpectedError() {
        // Arrange
        AuthRequest authRequest = new AuthRequest();
        authRequest.setDeviceID("device123");
        authRequest.setDeviceType(AuthRequest.DeviceTypeEnum.WATCH);

        when(restTemplate.exchange(
                any(String.class),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        DeviceRegistrationException exception = assertThrows(
                DeviceRegistrationException.class,
                () -> client.registerDevice(authRequest)
        );

        assertEquals("Failed to communicate with device registration API", exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception.getCause());
    }

    @Test
    void registerDevice_shouldIncludeAllDeviceTypes() throws DeviceRegistrationException {
        // Test all device types to ensure they're handled correctly
        AuthRequest.DeviceTypeEnum[] deviceTypes = {
            AuthRequest.DeviceTypeEnum.I_OS,
            AuthRequest.DeviceTypeEnum.ANDROID,
            AuthRequest.DeviceTypeEnum.WATCH,
            AuthRequest.DeviceTypeEnum.TV
        };

        for (AuthRequest.DeviceTypeEnum deviceType : deviceTypes) {
            AuthRequest authRequest = new AuthRequest();
            authRequest.setDeviceID("device-" + deviceType.getValue());
            authRequest.setDeviceType(deviceType);

            ResponseEntity<String> mockResponse = ResponseEntity.ok("Success");
            when(restTemplate.exchange(
                    any(String.class),
                    any(HttpMethod.class),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(mockResponse);

            int statusCode = client.registerDevice(authRequest);
            assertEquals(200, statusCode);
        }
    }
}
