package com.jss.devicemanager.common.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter that maps enum constants (IOS, ANDROID, etc.) to database values (iOS, Android, etc.).
 * This allows us to follow Java naming conventions (uppercase enum constants) while maintaining
 * backward compatibility with existing database values.
 */
@Converter(autoApply = true)
public class DeviceTypeConverter implements AttributeConverter<Device.DeviceType, String> {

    @Override
    public String convertToDatabaseColumn(Device.DeviceType deviceType) {
        if (deviceType == null) {
            return null;
        }
        return deviceType.getDbValue();
    }

    @Override
    public Device.DeviceType convertToEntityAttribute(String dbValue) {
        if (dbValue == null) {
            return null;
        }
        return Device.DeviceType.fromDbValue(dbValue);
    }
}
