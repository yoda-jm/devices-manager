package com.jss.devicemanager.common.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false, unique = true, length = 255)
    private String deviceId;

    @Convert(converter = DeviceTypeConverter.class)
    @Column(name = "device_type", nullable = false)
    private DeviceType deviceType;

    // Constructors
    public Device() {
    }

    public Device(String deviceId, DeviceType deviceType) {
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public enum DeviceType {
        IOS("iOS"),
        ANDROID("Android"),
        WATCH("Watch"),
        TV("TV");

        private final String dbValue;

        DeviceType(String dbValue) {
            this.dbValue = dbValue;
        }

        public String getDbValue() {
            return dbValue;
        }

        public static DeviceType fromDbValue(String dbValue) {
            for (DeviceType type : values()) {
                if (type.dbValue.equals(dbValue)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown device type: " + dbValue);
        }

        /**
         * Converts from API string value to DeviceType enum.
         * For the moment, API values and database values match (iOS, Android, Watch, TV),
         * so this method delegates to fromDbValue().
         */
        public static DeviceType fromApiValue(String apiValue) {
            return fromDbValue(apiValue);
        }
    }
}
