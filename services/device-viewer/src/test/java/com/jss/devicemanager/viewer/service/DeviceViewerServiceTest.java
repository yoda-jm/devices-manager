package com.jss.devicemanager.viewer.service;

import com.jss.devicemanager.common.entity.Device;
import com.jss.devicemanager.common.repository.DeviceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceViewerServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceViewerService deviceViewerService;

    @Test
    void getAllDevices_shouldReturnAllDevices() {
        // Given
        List<Device> devices = Arrays.asList(
                new Device("device1", Device.DeviceType.IOS),
                new Device("device2", Device.DeviceType.ANDROID)
        );
        when(deviceRepository.findAll()).thenReturn(devices);

        // When
        List<Device> result = deviceViewerService.getAllDevices();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(deviceRepository).findAll();
    }

    @Test
    void getAllDevicesPaged_shouldReturnPagedDevices() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Device> devices = Arrays.asList(
                new Device("device1", Device.DeviceType.IOS),
                new Device("device2", Device.DeviceType.ANDROID)
        );
        Page<Device> page = new PageImpl<>(devices, pageable, 2);
        when(deviceRepository.findAll(pageable)).thenReturn(page);

        // When
        Page<Device> result = deviceViewerService.getAllDevicesPaged(pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        verify(deviceRepository).findAll(pageable);
    }

    @Test
    void searchDevices_shouldReturnMatchingDevices() {
        // Given
        String searchTerm = "iOS";
        List<Device> devices = Arrays.asList(
                new Device("device1", Device.DeviceType.IOS),
                new Device("device2", Device.DeviceType.IOS)
        );
        when(deviceRepository.searchDevices(searchTerm)).thenReturn(devices);

        // When
        List<Device> result = deviceViewerService.searchDevices(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(deviceRepository).searchDevices(searchTerm);
    }

    @Test
    void searchDevices_shouldReturnAllDevicesWhenSearchIsNull() {
        // Given
        List<Device> devices = Arrays.asList(
                new Device("device1", Device.DeviceType.IOS),
                new Device("device2", Device.DeviceType.ANDROID)
        );
        when(deviceRepository.findAll()).thenReturn(devices);

        // When
        List<Device> result = deviceViewerService.searchDevices(null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(deviceRepository).findAll();
    }

    @Test
    void searchDevices_shouldReturnAllDevicesWhenSearchIsEmpty() {
        // Given
        List<Device> devices = Arrays.asList(
                new Device("device1", Device.DeviceType.IOS),
                new Device("device2", Device.DeviceType.ANDROID)
        );
        when(deviceRepository.findAll()).thenReturn(devices);

        // When
        List<Device> result = deviceViewerService.searchDevices("   ");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(deviceRepository).findAll();
    }

    @Test
    void getDeviceStatistics_shouldReturnStatisticsByDeviceType() {
        // Given
        List<Device> devices = Arrays.asList(
                new Device("device1", Device.DeviceType.IOS),
                new Device("device2", Device.DeviceType.IOS),
                new Device("device3", Device.DeviceType.ANDROID),
                new Device("device4", Device.DeviceType.WATCH)
        );
        when(deviceRepository.findAll()).thenReturn(devices);

        // When
        Map<Device.DeviceType, Long> result = deviceViewerService.getDeviceStatistics();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(2L, result.get(Device.DeviceType.IOS));
        assertEquals(1L, result.get(Device.DeviceType.ANDROID));
        assertEquals(1L, result.get(Device.DeviceType.WATCH));
        verify(deviceRepository).findAll();
    }

    @Test
    void getTotalCount_shouldReturnTotalDeviceCount() {
        // Given
        when(deviceRepository.count()).thenReturn(42L);

        // When
        long result = deviceViewerService.getTotalCount();

        // Then
        assertEquals(42L, result);
        verify(deviceRepository).count();
    }
}
