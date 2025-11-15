package com.jss.devicemanager.statistics.controller;

import com.jss.devicemanager.common.entity.Device;
import com.jss.devicemanager.common.exception.InvalidDeviceTypeException;
import com.jss.devicemanager.statistics.exception.BadGatewayException;
import com.jss.devicemanager.statistics.model.AuthRequest;
import com.jss.devicemanager.statistics.model.AuthResponse;
import com.jss.devicemanager.statistics.model.StatisticsResponse;
import com.jss.devicemanager.statistics.service.DeviceRegistrationClient;
import com.jss.devicemanager.statistics.service.DeviceRegistrationClient.DeviceRegistrationException;
import com.jss.devicemanager.statistics.service.StatisticsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsControllerTest {

    @Mock
    private StatisticsService statisticsService;

    @Mock
    private DeviceRegistrationClient deviceRegistrationClient;

    @InjectMocks
    private StatisticsController statisticsController;

    @Test
    void getStatistics_shouldReturnDeviceCount() {
        // Given
        String deviceType = "iOS";
        when(statisticsService.getDeviceCountByType(Device.DeviceType.iOS)).thenReturn(42L);

        // When
        ResponseEntity<StatisticsResponse> response = statisticsController.getStatistics(deviceType);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(StatisticsResponse.DeviceTypeEnum.I_OS, response.getBody().getDeviceType());
        assertEquals(42, response.getBody().getCount());
    }

    @Test
    void getStatistics_shouldThrowInvalidDeviceTypeExceptionForInvalidDeviceType() {
        // Given
        String invalidDeviceType = "InvalidType";

        // When & Then
        InvalidDeviceTypeException exception = assertThrows(InvalidDeviceTypeException.class,
            () -> statisticsController.getStatistics(invalidDeviceType));

        assertEquals("Invalid device type: InvalidType", exception.getMessage());
        assertEquals("InvalidType", exception.getDeviceType());
    }

    @Test
    void logAuth_shouldReturnSuccessMessageWhenDeviceIsNewlyRegistered() throws DeviceRegistrationException {
        // Given
        AuthRequest authRequest = new AuthRequest();
        authRequest.setDeviceID("device123");
        authRequest.setDeviceType(AuthRequest.DeviceTypeEnum.I_OS);

        when(deviceRegistrationClient.registerDevice(authRequest)).thenReturn(200);

        // When
        ResponseEntity<AuthResponse> response = statisticsController.logAuth(authRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Device successfully registered", response.getBody().getMessage());
        assertEquals("device123", response.getBody().getDeviceID());
        assertEquals(AuthResponse.DeviceTypeEnum.I_OS, response.getBody().getDeviceType());
    }

    @Test
    void logAuth_shouldReturnAlreadyRegisteredMessageWhenDeviceExists() throws DeviceRegistrationException {
        // Given
        AuthRequest authRequest = new AuthRequest();
        authRequest.setDeviceID("device123");
        authRequest.setDeviceType(AuthRequest.DeviceTypeEnum.I_OS);

        when(deviceRegistrationClient.registerDevice(authRequest)).thenReturn(409);

        // When
        ResponseEntity<AuthResponse> response = statisticsController.logAuth(authRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Device already registered", response.getBody().getMessage());
        assertEquals("device123", response.getBody().getDeviceID());
        assertEquals(AuthResponse.DeviceTypeEnum.I_OS, response.getBody().getDeviceType());
    }

    @Test
    void logAuth_shouldThrowBadGatewayExceptionWhenRegistrationClientFails() throws DeviceRegistrationException {
        // Given
        AuthRequest authRequest = new AuthRequest();
        authRequest.setDeviceID("device123");
        authRequest.setDeviceType(AuthRequest.DeviceTypeEnum.I_OS);

        when(deviceRegistrationClient.registerDevice(authRequest))
            .thenThrow(new DeviceRegistrationException("Connection failed", null));

        // When & Then
        BadGatewayException exception = assertThrows(BadGatewayException.class,
            () -> statisticsController.logAuth(authRequest));

        assertEquals("Connection failed", exception.getMessage());
    }

    @Test
    void logAuth_shouldThrowBadGatewayExceptionWhenUnexpectedStatusCodeReturned() throws DeviceRegistrationException {
        // Given
        AuthRequest authRequest = new AuthRequest();
        authRequest.setDeviceID("device123");
        authRequest.setDeviceType(AuthRequest.DeviceTypeEnum.I_OS);

        when(deviceRegistrationClient.registerDevice(authRequest)).thenReturn(500);

        // When & Then
        BadGatewayException exception = assertThrows(BadGatewayException.class,
            () -> statisticsController.logAuth(authRequest));

        assertEquals("Device registration API returned unexpected status: 500", exception.getMessage());
    }
}
