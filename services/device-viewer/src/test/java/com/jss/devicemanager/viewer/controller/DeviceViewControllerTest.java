package com.jss.devicemanager.viewer.controller;

import com.jss.devicemanager.common.entity.Device;
import com.jss.devicemanager.viewer.service.DeviceViewerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceViewControllerTest {

    @Mock
    private DeviceViewerService deviceViewerService;

    @InjectMocks
    private DeviceViewController deviceViewController;

    @Test
    void index_shouldReturnIndexHtmlPage() {
        // When
        String result = deviceViewController.index();

        // Then
        assertEquals("index.html", result);
    }

    @Test
    void getDevices_shouldReturnAllDevicesWhenNoSearchProvided() {
        // Given
        List<Device> devices = Arrays.asList(
                createDevice(1L, "device1", Device.DeviceType.IOS),
                createDevice(2L, "device2", Device.DeviceType.ANDROID)
        );
        when(deviceViewerService.searchDevices(null)).thenReturn(devices);

        // When
        ResponseEntity<List<DeviceDTO>> response = deviceViewController.getDevices(null);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("device1", response.getBody().get(0).deviceId());
        assertEquals("IOS", response.getBody().get(0).deviceType());
        verify(deviceViewerService).searchDevices(null);
    }

    @Test
    void getDevices_shouldReturnFilteredDevicesWhenSearchProvided() {
        // Given
        String searchTerm = "iOS";
        List<Device> devices = Arrays.asList(
                createDevice(1L, "device1", Device.DeviceType.IOS),
                createDevice(3L, "device3", Device.DeviceType.IOS)
        );
        when(deviceViewerService.searchDevices(searchTerm)).thenReturn(devices);

        // When
        ResponseEntity<List<DeviceDTO>> response = deviceViewController.getDevices(searchTerm);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("device1", response.getBody().get(0).deviceId());
        assertEquals("IOS", response.getBody().get(0).deviceType());
        verify(deviceViewerService).searchDevices(searchTerm);
    }

    @Test
    void getDevices_shouldReturnEmptyListWhenNoDevicesFound() {
        // Given
        String searchTerm = "nonexistent";
        when(deviceViewerService.searchDevices(searchTerm)).thenReturn(Arrays.asList());

        // When
        ResponseEntity<List<DeviceDTO>> response = deviceViewController.getDevices(searchTerm);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
        verify(deviceViewerService).searchDevices(searchTerm);
    }

    @Test
    void getStatistics_shouldReturnTotalCountAndStatisticsByType() {
        // Given
        Map<Device.DeviceType, Long> statsByType = new EnumMap<>(Device.DeviceType.class);
        statsByType.put(Device.DeviceType.IOS, 10L);
        statsByType.put(Device.DeviceType.ANDROID, 5L);
        statsByType.put(Device.DeviceType.WATCH, 2L);

        when(deviceViewerService.getTotalCount()).thenReturn(17L);
        when(deviceViewerService.getDeviceStatistics()).thenReturn(statsByType);

        // When
        ResponseEntity<Map<String, Object>> response = deviceViewController.getStatistics();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        assertEquals(17L, response.getBody().get("total"));

        @SuppressWarnings("unchecked")
        Map<String, Long> byType = (Map<String, Long>) response.getBody().get("byType");
        assertNotNull(byType);
        assertEquals(3, byType.size());
        assertEquals(10L, byType.get("IOS"));
        assertEquals(5L, byType.get("ANDROID"));
        assertEquals(2L, byType.get("WATCH"));

        verify(deviceViewerService).getTotalCount();
        verify(deviceViewerService).getDeviceStatistics();
    }

    @Test
    void getStatistics_shouldReturnZeroWhenNoDevicesExist() {
        // Given
        when(deviceViewerService.getTotalCount()).thenReturn(0L);
        when(deviceViewerService.getDeviceStatistics()).thenReturn(new EnumMap<>(Device.DeviceType.class));

        // When
        ResponseEntity<Map<String, Object>> response = deviceViewController.getStatistics();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(0L, response.getBody().get("total"));

        @SuppressWarnings("unchecked")
        Map<String, Long> byType = (Map<String, Long>) response.getBody().get("byType");
        assertNotNull(byType);
        assertEquals(0, byType.size());

        verify(deviceViewerService).getTotalCount();
        verify(deviceViewerService).getDeviceStatistics();
    }

    // Helper method to create Device with ID
    private Device createDevice(Long id, String deviceId, Device.DeviceType deviceType) {
        Device device = new Device(deviceId, deviceType);
        device.setId(id);
        return device;
    }
}
