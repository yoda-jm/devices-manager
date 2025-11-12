package com.jss.devicemanager.registration.controller;

import com.jss.devicemanager.registration.model.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {

    @InjectMocks
    private RegistrationController registrationController;

    @Test
    void registerDevice_shouldThrowUnsupportedOperationException() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setDeviceID("device456");
        registerRequest.setDeviceType(RegisterRequest.DeviceTypeEnum.ANDROID);

        // When
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
            () -> registrationController.registerDevice(registerRequest));

        // Then
        assertEquals("Not implemented yet", exception.getMessage());
    }
}
