package com.jss.devicemanager.statistics.controller;

import com.jss.devicemanager.common.entity.Device;
import com.jss.devicemanager.common.exception.InvalidDeviceTypeException;
import com.jss.devicemanager.statistics.api.StatisticsApi;
import com.jss.devicemanager.statistics.exception.BadGatewayException;
import com.jss.devicemanager.statistics.model.AuthRequest;
import com.jss.devicemanager.statistics.model.AuthResponse;
import com.jss.devicemanager.statistics.model.StatisticsResponse;
import com.jss.devicemanager.statistics.service.DeviceRegistrationClient;
import com.jss.devicemanager.statistics.service.DeviceRegistrationClient.DeviceRegistrationException;
import com.jss.devicemanager.statistics.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController implements StatisticsApi {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    private final StatisticsService statisticsService;
    private final DeviceRegistrationClient deviceRegistrationClient;

    public StatisticsController(
            StatisticsService statisticsService,
            DeviceRegistrationClient deviceRegistrationClient) {
        this.statisticsService = statisticsService;
        this.deviceRegistrationClient = deviceRegistrationClient;
    }

    @Override
    public ResponseEntity<StatisticsResponse> getStatistics(String deviceType) {
        logger.debug("getStatistics called with deviceType={}", deviceType);

        // Validate and convert device type
        StatisticsResponse.DeviceTypeEnum apiDeviceType;
        try {
            apiDeviceType = StatisticsResponse.DeviceTypeEnum.fromValue(deviceType);
        } catch (IllegalArgumentException e) {
            // Invalid device type - throw exception handled by GlobalExceptionHandler
            throw new InvalidDeviceTypeException(deviceType);
        }

        // Convert API DeviceType to Entity DeviceType
        Device.DeviceType entityDeviceType = Device.DeviceType.valueOf(apiDeviceType.getValue());

        // Query database
        long count = statisticsService.getDeviceCountByType(entityDeviceType);

        // Build response
        StatisticsResponse response = new StatisticsResponse();
        response.setDeviceType(apiDeviceType);
        response.setCount((int) count);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AuthResponse> logAuth(AuthRequest authRequest) {
        logger.debug("logAuth called with deviceID={}, deviceType={}",
                authRequest.getDeviceID(), authRequest.getDeviceType());

        // Forward the registration request to device-registration-api
        try {
            int statusCode = deviceRegistrationClient.registerDevice(authRequest);

            // Build response
            AuthResponse response = new AuthResponse();
            response.setDeviceID(authRequest.getDeviceID());
            // Convert enum types
            response.setDeviceType(AuthResponse.DeviceTypeEnum.valueOf(authRequest.getDeviceType().name()));

            // Handle different status codes
            if (statusCode == 200) {
                response.setMessage("Device successfully registered");
                logger.info("Device '{}' of type '{}' successfully registered",
                        authRequest.getDeviceID(), authRequest.getDeviceType());
            } else if (statusCode == 409) {
                // Device already exists - note that we cannot know at this point if the
                // already registered device has the correct type. Type mismatch warnings
                // are logged by device-registration-api.
                response.setMessage("Device already registered");
                logger.debug("Device '{}' already registered", authRequest.getDeviceID());
            } else {
                // Unexpected status code
                logger.warn("Unexpected status code {} from device registration API", statusCode);
                throw new BadGatewayException(
                        "Device registration API returned unexpected status: " + statusCode, null);
            }

            return ResponseEntity.ok(response);

        } catch (DeviceRegistrationException e) {
            // Connection failed or API error - return 502
            throw new BadGatewayException(e.getMessage(), e);
        }
    }
}
