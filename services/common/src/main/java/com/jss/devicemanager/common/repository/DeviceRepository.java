package com.jss.devicemanager.common.repository;

import com.jss.devicemanager.common.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByDeviceId(String deviceId);
}
