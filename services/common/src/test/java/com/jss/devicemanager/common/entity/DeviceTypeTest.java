package com.jss.devicemanager.common.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class DeviceTypeTest {

    @ParameterizedTest
    @CsvSource({
        "IOS, iOS",
        "ANDROID, Android",
        "WATCH, Watch",
        "TV, TV"
    })
    void getDbValue_shouldReturnCorrectValue(Device.DeviceType type, String expectedDbValue) {
        assertEquals(expectedDbValue, type.getDbValue());
    }

    @ParameterizedTest
    @CsvSource({
        "iOS, IOS",
        "Android, ANDROID",
        "Watch, WATCH",
        "TV, TV"
    })
    void fromDbValue_shouldReturnCorrectType(String dbValue, Device.DeviceType expectedType) {
        assertEquals(expectedType, Device.DeviceType.fromDbValue(dbValue));
    }

    @ParameterizedTest
    @ValueSource(strings = {"PlayStation", "Tablet", "Computer", "Xbox"})
    void fromDbValue_shouldThrowException_forInvalidValue(String invalidValue) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Device.DeviceType.fromDbValue(invalidValue)
        );
        assertTrue(exception.getMessage().contains("Unknown device type"));
        assertTrue(exception.getMessage().contains(invalidValue));
    }

    @Test
    void fromDbValue_shouldThrowException_forNull() {
        // The implementation iterates over values and compares with equals(),
        // which handles null gracefully and throws IllegalArgumentException
        assertThrows(
                IllegalArgumentException.class,
                () -> Device.DeviceType.fromDbValue(null)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"ios", "ANDROID", "watch", "tv"})
    void fromDbValue_shouldBeCaseSensitive(String invalidCase) {
        assertThrows(
                IllegalArgumentException.class,
                () -> Device.DeviceType.fromDbValue(invalidCase)
        );
    }

    @ParameterizedTest
    @CsvSource({
        "iOS, IOS",
        "Android, ANDROID",
        "Watch, WATCH",
        "TV, TV"
    })
    void fromApiValue_shouldReturnCorrectType(String apiValue, Device.DeviceType expectedType) {
        assertEquals(expectedType, Device.DeviceType.fromApiValue(apiValue));
    }

    @ParameterizedTest
    @ValueSource(strings = {"iOS", "Android", "Watch", "TV"})
    void fromApiValue_shouldMatchFromDbValue(String value) {
        // Since fromApiValue delegates to fromDbValue, they should produce the same results
        assertEquals(
                Device.DeviceType.fromDbValue(value),
                Device.DeviceType.fromApiValue(value)
        );
    }

    @Test
    void values_shouldContainAllFourTypes() {
        Device.DeviceType[] values = Device.DeviceType.values();
        assertEquals(4, values.length);
        assertTrue(java.util.Arrays.asList(values).contains(Device.DeviceType.IOS));
        assertTrue(java.util.Arrays.asList(values).contains(Device.DeviceType.ANDROID));
        assertTrue(java.util.Arrays.asList(values).contains(Device.DeviceType.WATCH));
        assertTrue(java.util.Arrays.asList(values).contains(Device.DeviceType.TV));
    }

    @ParameterizedTest
    @ValueSource(strings = {"IOS", "ANDROID", "WATCH", "TV"})
    void valueOf_shouldWorkForEnumConstants(String constantName) {
        // valueOf should succeed for valid enum constant names
        assertNotNull(Device.DeviceType.valueOf(constantName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"iOS", "Android", "Watch", "Tv", "tv", "invalid", ""})
    void valueOf_shouldThrowException_forInvalidConstants(String invalidConstant) {
        // valueOf should throw exception for anything that's not an exact enum constant name
        // Valid enum constants are: IOS, ANDROID, WATCH, TV (all uppercase)
        assertThrows(
                IllegalArgumentException.class,
                () -> Device.DeviceType.valueOf(invalidConstant)
        );
    }

    @Test
    void allTypes_shouldHaveUniqueDbValues() {
        // Verify no duplicate database values
        java.util.Set<String> dbValues = new java.util.HashSet<>();
        for (Device.DeviceType type : Device.DeviceType.values()) {
            assertTrue(
                    dbValues.add(type.getDbValue()),
                    "Duplicate dbValue found: " + type.getDbValue()
            );
        }
    }

    @Test
    void roundTrip_shouldPreserveValue() {
        // Verify round-trip: enum -> dbValue -> enum
        for (Device.DeviceType type : Device.DeviceType.values()) {
            String dbValue = type.getDbValue();
            Device.DeviceType recovered = Device.DeviceType.fromDbValue(dbValue);
            assertEquals(type, recovered, "Round-trip failed for: " + type);
        }
    }
}
