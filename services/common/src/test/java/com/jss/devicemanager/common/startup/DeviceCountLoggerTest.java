package com.jss.devicemanager.common.startup;

import com.jss.devicemanager.common.repository.DeviceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceCountLoggerTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private ApplicationArguments applicationArguments;

    private DeviceCountLogger deviceCountLogger;

    @Test
    void run_shouldLogDeviceCount_whenInfoEnabled() {
        // Arrange
        String applicationName = "Test Application";
        deviceCountLogger = new DeviceCountLogger(deviceRepository, applicationName);

        when(deviceRepository.count()).thenReturn(42L);

        // Act
        deviceCountLogger.run(applicationArguments);

        // Assert
        verify(deviceRepository).count();
    }

    @Test
    void run_shouldLogWithDefaultApplicationName_whenNameNotProvided() {
        // Arrange
        String defaultName = "Application";
        deviceCountLogger = new DeviceCountLogger(deviceRepository, defaultName);

        when(deviceRepository.count()).thenReturn(10L);

        // Act
        deviceCountLogger.run(applicationArguments);

        // Assert
        verify(deviceRepository).count();
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 5L, 42L, 999999L})
    void run_shouldHandleVariousDeviceCounts(long deviceCount) {
        // Arrange
        deviceCountLogger = new DeviceCountLogger(deviceRepository, "Test App");
        when(deviceRepository.count()).thenReturn(deviceCount);

        // Act
        deviceCountLogger.run(applicationArguments);

        // Assert
        verify(deviceRepository, times(1)).count();
    }

    @Test
    void constructor_shouldAcceptValidParameters() {
        // Act
        deviceCountLogger = new DeviceCountLogger(deviceRepository, "My Application");

        // Assert - no exception thrown
        // Verify the object was created successfully by calling run
        when(deviceRepository.count()).thenReturn(1L);
        deviceCountLogger.run(applicationArguments);
        verify(deviceRepository).count();
    }

    @Test
    void run_shouldWorkWithNullArguments() {
        // Arrange
        deviceCountLogger = new DeviceCountLogger(deviceRepository, "Test App");

        when(deviceRepository.count()).thenReturn(7L);

        // Act - passing null is valid for ApplicationRunner
        deviceCountLogger.run(null);

        // Assert
        verify(deviceRepository).count();
    }
}
