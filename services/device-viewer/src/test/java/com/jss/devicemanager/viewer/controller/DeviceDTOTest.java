package com.jss.devicemanager.viewer.controller;

import com.jss.devicemanager.common.entity.Device;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DeviceDTOTest {

    @ParameterizedTest
    @CsvSource({
            "42, device123, IOS",
            "1, android-device, ANDROID",
            "2, watch-device, WATCH",
            "3, tv-device, TV"
    })
    void fromEntity_shouldConvertDeviceToDTO(Long id, String deviceId, Device.DeviceType deviceType) {
        // Given
        Device device = new Device(deviceId, deviceType);
        device.setId(id);

        // When
        DeviceDTO dto = DeviceDTO.fromEntity(device);

        // Then
        assertNotNull(dto);
        assertEquals(id, dto.id());
        assertEquals(deviceId, dto.deviceId());
        assertEquals(deviceType.name(), dto.deviceType());
    }
}
