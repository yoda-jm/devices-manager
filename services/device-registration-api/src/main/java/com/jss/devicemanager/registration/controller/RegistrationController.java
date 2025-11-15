package com.jss.devicemanager.registration.controller;

import com.jss.devicemanager.common.entity.Device;
import com.jss.devicemanager.common.exception.InvalidDeviceTypeException;
import com.jss.devicemanager.common.security.RequireApiKey;
import com.jss.devicemanager.registration.api.RegistrationApi;
import com.jss.devicemanager.registration.exception.DuplicateDeviceException;
import com.jss.devicemanager.registration.model.RegisterRequest;
import com.jss.devicemanager.registration.model.RegisterResponse;
import com.jss.devicemanager.registration.service.DeviceRegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequireApiKey
public class RegistrationController implements RegistrationApi {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    private final DeviceRegistrationService registrationService;

    public RegistrationController(DeviceRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Override
    public ResponseEntity<RegisterResponse> registerDevice(RegisterRequest registerRequest) {
        logger.debug("registerDevice called with deviceID={}, deviceType={}",
                registerRequest.getDeviceID(), registerRequest.getDeviceType());

        // Validate and convert device type
        RegisterRequest.DeviceTypeEnum apiDeviceType = registerRequest.getDeviceType();
        if (apiDeviceType == null) {
            // Invalid device type - throw exception handled by GlobalExceptionHandler
            throw new InvalidDeviceTypeException("null");
        }

        // Convert to entity device type
        Device.DeviceType entityDeviceType = Device.DeviceType.valueOf(apiDeviceType.getValue());

        // Register device
        try {
            registrationService.registerDevice(registerRequest.getDeviceID(), entityDeviceType);

            // Build success response
            RegisterResponse response = new RegisterResponse();
            response.setDeviceID(registerRequest.getDeviceID());
            // Convert from RegisterRequest.DeviceTypeEnum to RegisterResponse.DeviceTypeEnum
            response.setDeviceType(RegisterResponse.DeviceTypeEnum.valueOf(apiDeviceType.name()));
            response.setRegisteredAt(java.time.OffsetDateTime.now());

            return ResponseEntity.ok(response);
        } catch (DataIntegrityViolationException e) {
            // Duplicate device_id - throw exception handled by GlobalExceptionHandler
            throw new DuplicateDeviceException(registerRequest.getDeviceID());
        }
    }
}
