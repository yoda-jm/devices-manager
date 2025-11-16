package com.jss.devicemanager.viewer.controller;

import com.jss.devicemanager.common.entity.Device;

public record DeviceDTO(
        Long id,
        String deviceId,
        String deviceType
) {
    public static DeviceDTO fromEntity(Device device) {
        return new DeviceDTO(
                device.getId(),
                device.getDeviceId(),
                device.getDeviceType().name()
        );
    }
}
