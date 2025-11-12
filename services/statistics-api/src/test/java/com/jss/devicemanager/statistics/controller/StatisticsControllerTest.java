package com.jss.devicemanager.statistics.controller;

import com.jss.devicemanager.statistics.model.AuthRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class StatisticsControllerTest {

    @InjectMocks
    private StatisticsController statisticsController;

    @Test
    void getStatistics_shouldThrowUnsupportedOperationException() {
        // Given
        String deviceType = "iOS";

        // When
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
            () -> statisticsController.getStatistics(deviceType));

        // Then
        assertEquals("Not implemented yet", exception.getMessage());
    }

    @Test
    void logAuth_shouldThrowUnsupportedOperationException() {
        // Given
        AuthRequest authRequest = new AuthRequest();
        authRequest.setDeviceID("device123");
        authRequest.setDeviceType(AuthRequest.DeviceTypeEnum.I_OS);

        // When
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
            () -> statisticsController.logAuth(authRequest));

        // Then
        assertEquals("Not implemented yet", exception.getMessage());
    }
}
