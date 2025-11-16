package com.jss.devicemanager.registration.controller;

import com.jss.devicemanager.common.entity.Device;
import com.jss.devicemanager.registration.exception.DuplicateDeviceException;
import com.jss.devicemanager.registration.model.RegisterRequest;
import com.jss.devicemanager.registration.model.RegisterResponse;
import com.jss.devicemanager.registration.service.DeviceRegistrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {

    @Mock
    private DeviceRegistrationService registrationService;

    @InjectMocks
    private RegistrationController registrationController;

    @Test
    void registerDevice_shouldReturnSuccessResponse() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setDeviceID("device123");
        request.setDeviceType(RegisterRequest.DeviceTypeEnum.I_OS);

        // When
        ResponseEntity<RegisterResponse> response = registrationController.registerDevice(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("device123", response.getBody().getDeviceID());
        assertEquals(RegisterResponse.DeviceTypeEnum.I_OS, response.getBody().getDeviceType());
        assertNotNull(response.getBody().getRegisteredAt());

        verify(registrationService).registerDevice("device123", Device.DeviceType.IOS);
    }


    @Test
    void registerDevice_shouldThrowDuplicateDeviceExceptionForDuplicateDeviceID() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setDeviceID("device123");
        request.setDeviceType(RegisterRequest.DeviceTypeEnum.ANDROID);

        doThrow(new DataIntegrityViolationException("Duplicate key"))
            .when(registrationService).registerDevice(eq("device123"), any());

        // When & Then
        DuplicateDeviceException exception = assertThrows(DuplicateDeviceException.class,
            () -> registrationController.registerDevice(request));

        assertEquals("Device with ID already exists: device123", exception.getMessage());
        assertEquals("device123", exception.getDeviceId());
    }
}
