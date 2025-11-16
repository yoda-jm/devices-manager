package com.jss.devicemanager.viewer.service;

import com.jss.devicemanager.common.entity.Device;
import com.jss.devicemanager.common.repository.DeviceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DeviceViewerService {

    private final DeviceRepository deviceRepository;

    public DeviceViewerService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    public Page<Device> getAllDevicesPaged(Pageable pageable) {
        return deviceRepository.findAll(pageable);
    }

    public List<Device> searchDevices(String search) {
        if (search == null || search.trim().isEmpty()) {
            return getAllDevices();
        }
        return deviceRepository.searchDevices(search.trim());
    }

    public Map<Device.DeviceType, Long> getDeviceStatistics() {
        List<Device> allDevices = deviceRepository.findAll();
        return allDevices.stream()
                .collect(Collectors.groupingBy(
                        Device::getDeviceType,
                        Collectors.counting()
                ));
    }

    public long getTotalCount() {
        return deviceRepository.count();
    }
}
