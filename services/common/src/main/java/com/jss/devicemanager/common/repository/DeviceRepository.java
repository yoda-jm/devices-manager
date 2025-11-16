package com.jss.devicemanager.common.repository;

import com.jss.devicemanager.common.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {

    Optional<Device> findByDeviceId(String deviceId);

    @Query("SELECT d FROM Device d WHERE " +
            "LOWER(d.deviceId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(CAST(d.deviceType AS string)) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Device> searchDevices(@Param("search") String search);

    List<Device> findByDeviceType(Device.DeviceType deviceType);
}
