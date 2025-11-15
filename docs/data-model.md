# Database Schema - Device Manager

## Purpose

Simple incremental schema to demonstrate Flyway migration capabilities. Start minimal, add fields progressively through migrations.

---

## V1 - Initial Schema (Current)

```sql
CREATE TABLE devices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id VARCHAR(255) NOT NULL,
    device_type ENUM('iOS', 'Android', 'Watch', 'TV') NOT NULL,

    UNIQUE KEY uk_device_id (device_id),
    INDEX idx_device_type (device_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Why minimal?** Intentionally omitting `registered_at` and `last_seen` to demonstrate adding NOT NULL columns via Flyway migration.

---

## V2 - Add Timestamps (Planned)

**Migration Challenge:** Adding NOT NULL columns to existing table with data requires DEFAULT values.

```sql
-- V2__add_timestamps.sql
ALTER TABLE devices
    ADD COLUMN registered_at TIMESTAMP NOT NULL DEFAULT '1970-01-01 00:00:00',
    ADD COLUMN last_seen TIMESTAMP NOT NULL DEFAULT '1970-01-01 00:00:00',
    ADD INDEX idx_registered_at (registered_at);

-- Update trigger for last_seen
ALTER TABLE devices
    MODIFY COLUMN last_seen TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
```

**Key Points:**
- `DEFAULT '1970-01-01'` (Unix epoch) marks migrated data - obviously not a real registration date
- Easy to discriminate: `WHERE registered_at > '1970-01-01 00:00:01'` excludes migrated devices
- New registrations get actual `CURRENT_TIMESTAMP` via application code
- Demonstrates real-world migration constraint handling

---

## V3 - Authentication History (Future)

Optional table for tracking all auth events (not just first registration):

```sql
CREATE TABLE auth_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id VARCHAR(255) NOT NULL,
    logged_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_device_id (device_id),
    FOREIGN KEY (device_id) REFERENCES devices(device_id) ON DELETE CASCADE
) ENGINE=InnoDB;
```

**Use case:** Analytics on authentication frequency, user engagement patterns.

---

## Key Design Decisions

| Decision | Rationale |
|----------|-----------|
| ENUM for device_type | Database-level constraint, matches API spec exactly |
| UNIQUE on device_id | Enables 409 Conflict detection via constraint violation |
| INDEX on device_type | Fast COUNT(*) for statistics query |
| Surrogate id | Standard practice, keeps device_id as business key |
| utf8mb4 charset | Full Unicode support (including emojis) |

---

## Queries

**Register device:**
```sql
INSERT INTO devices (device_id, device_type) VALUES (?, ?);
-- Duplicate throws: java.sql.SQLIntegrityConstraintViolationException
```

**Get statistics:**
```sql
SELECT COUNT(*) FROM devices WHERE device_type = ?;
-- Uses idx_device_type, returns 0 if no devices
```

---

## Migration Challenges & Solutions

### Challenge 1: Adding NOT NULL Columns
**Problem:** Cannot add NOT NULL column without value for existing rows
**Solution:** Use DEFAULT value in ALTER TABLE
**Limitation:** Existing rows get migration timestamp, not true registration time

### Challenge 2: ENUM Type Evolution
**Problem:** Adding new device type requires ALTER TABLE
**Solution:** Use MariaDB ENUM ALTER (or migrate to VARCHAR + CHECK constraint)

### Challenge 3: Discriminating Migrated Data
**Problem:** V2 migration can't know true registration time for existing devices
**Solution:** Use sentinel value `'1970-01-01'` (Unix epoch) as default
**Benefits:**
- Easily filterable: `WHERE registered_at > '1970-01-01 00:00:01'`
- Reportable: "X devices have unknown registration time"
- Better than `CURRENT_TIMESTAMP` which looks like real data

---

## Flyway Configuration

**Location:** `src/main/resources/db/migration/`

**Naming:**
- `V1__create_devices_table.sql`
- `V2__add_timestamps.sql`
- `V3__create_auth_logs_table.sql`

**Properties:**
```yaml
spring.flyway:
  enabled: true
  baseline-on-migrate: true  # Allow Flyway on existing database
```
