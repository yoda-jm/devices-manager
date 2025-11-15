package com.jss.devicemanager.statistics.service;

import com.jss.devicemanager.statistics.model.AuthRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class DeviceRegistrationClient {

    private static final Logger logger = LoggerFactory.getLogger(DeviceRegistrationClient.class);

    private final RestTemplate restTemplate;
    private final String deviceRegistrationApiUrl;
    private final String apiKey;

    public DeviceRegistrationClient(
            RestTemplate restTemplate,
            @Value("${device.registration.api.url}") String deviceRegistrationApiUrl,
            @Value("${device.registration.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.deviceRegistrationApiUrl = deviceRegistrationApiUrl;
        this.apiKey = apiKey;
    }

    /**
     * Register a device with the device-registration-api.
     *
     * @param authRequest The authentication request containing device info
     * @return HTTP status code from the registration API
     * @throws DeviceRegistrationException if the registration API is unavailable or fails
     */
    public int registerDevice(AuthRequest authRequest) throws DeviceRegistrationException {
        String url = deviceRegistrationApiUrl + "/Device/register";

        logger.debug("Forwarding device registration to {} for deviceID={}", url, authRequest.getDeviceID());

        // Build request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("deviceID", authRequest.getDeviceID());
        requestBody.put("deviceType", authRequest.getDeviceType().getValue());

        // Build headers with API key
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Device-Registration-API-Key", apiKey);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            logger.debug("Device registration response: status={}", response.getStatusCode());
            return response.getStatusCode().value();

        } catch (HttpClientErrorException e) {
            // 4xx errors (like 409 Conflict) are expected
            logger.debug("Device registration returned client error: status={}", e.getStatusCode());
            return e.getStatusCode().value();

        } catch (HttpServerErrorException e) {
            // 5xx errors from the registration API
            logger.error("Device registration API returned server error: status={}", e.getStatusCode());
            throw new DeviceRegistrationException("Device registration API returned error: " + e.getStatusCode(), e);

        } catch (ResourceAccessException e) {
            // Connection refused, timeout, etc.
            logger.error("Failed to connect to device registration API: {}", e.getMessage());
            throw new DeviceRegistrationException("Device registration API is unavailable", e);

        } catch (Exception e) {
            // Any other unexpected errors
            logger.error("Unexpected error calling device registration API", e);
            throw new DeviceRegistrationException("Failed to communicate with device registration API", e);
        }
    }

    public static class DeviceRegistrationException extends Exception {
        public DeviceRegistrationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
