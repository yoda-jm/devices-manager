package com.jss.devicemanager.registration.service;

import com.jss.devicemanager.common.entity.Device;
import com.jss.devicemanager.common.repository.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeviceRegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceRegistrationService.class);

    private final DeviceRepository deviceRepository;

    public DeviceRegistrationService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    /**
     * Register a new device or return existing one.
     *
     * @param deviceId   Unique device identifier
     * @param deviceType Type of device
     * @return true if newly created, false if already exists
     * @throws DataIntegrityViolationException if duplicate deviceId (409 Conflict)
     */
    public boolean registerDevice(String deviceId, Device.DeviceType deviceType) {
        try {
            Device device = new Device(deviceId, deviceType);
            deviceRepository.save(device);
            logger.info("Device '{}' of type '{}' successfully registered", deviceId, deviceType);
            return true; // Newly created
        } catch (DataIntegrityViolationException e) {
            // Duplicate device_id unique constraint violation
            // Check if the existing device has a different type
            Optional<Device> existingDevice = deviceRepository.findByDeviceId(deviceId);
            if (existingDevice.isPresent() && existingDevice.get().getDeviceType() != deviceType) {
                logger.warn("Conflict: Device '{}' already exists with type '{}', but registration attempted with type '{}'",
                        deviceId, existingDevice.get().getDeviceType(), deviceType);
            }
            throw e;
        }
    }
}
