-- V1: Initial schema - Minimal device tracking
-- Intentionally omitting registered_at and last_seen to demonstrate Flyway migrations (V2)

CREATE TABLE devices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id VARCHAR(255) NOT NULL,
    device_type ENUM('iOS', 'Android', 'Watch', 'TV') NOT NULL,

    UNIQUE KEY uk_device_id (device_id),
    INDEX idx_device_type (device_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
