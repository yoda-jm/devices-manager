package com.jss.devicemanager.statistics.service;

import com.jss.devicemanager.common.entity.Device;
import com.jss.devicemanager.common.repository.DeviceRepository;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    private final DeviceRepository deviceRepository;

    public StatisticsService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public long getDeviceCountByType(Device.DeviceType deviceType) {
        return deviceRepository.findAll().stream()
                .filter(device -> device.getDeviceType() == deviceType)
                .count();
    }
}
