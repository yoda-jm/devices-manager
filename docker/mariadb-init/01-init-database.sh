#!/bin/bash
# Initialize database and users with least privilege principle
# This script runs once when the MariaDB container is first created
# Uses environment variables from docker-compose.yml

set -e

echo "Initializing database with least privilege users..."

# Execute SQL using environment variables
mariadb -u root -p"${MYSQL_ROOT_PASSWORD}" <<-EOSQL
    -- Create database (if not already created by MYSQL_DATABASE env var)
    CREATE DATABASE IF NOT EXISTS ${MYSQL_DATABASE} CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

    -- ============================================================================
    -- User 1: Flyway Migration User (Full schema management rights)
    -- ============================================================================
    -- Purpose: Apply database migrations, manage schema changes
    -- Permissions: Full DDL (CREATE, ALTER, DROP) and DML (SELECT, INSERT, UPDATE, DELETE)
    CREATE USER IF NOT EXISTS '${MYSQL_FLYWAY_USER}'@'%' IDENTIFIED BY '${MYSQL_FLYWAY_PASSWORD}';
    GRANT ALL PRIVILEGES ON ${MYSQL_DATABASE}.* TO '${MYSQL_FLYWAY_USER}'@'%';
    GRANT SELECT ON mysql.proc TO '${MYSQL_FLYWAY_USER}'@'%';

    -- ============================================================================
    -- User 2: Application User for Device Registration (Read/Write)
    -- ============================================================================
    -- Purpose: Register devices, update device records
    -- Permissions: DML only (SELECT, INSERT, UPDATE, DELETE) - no schema changes
    CREATE USER IF NOT EXISTS '${MYSQL_REGISTRATION_USER}'@'%' IDENTIFIED BY '${MYSQL_REGISTRATION_PASSWORD}';
    GRANT SELECT, INSERT, UPDATE, DELETE ON ${MYSQL_DATABASE}.* TO '${MYSQL_REGISTRATION_USER}'@'%';

    -- ============================================================================
    -- User 3: Statistics User (Read-Only)
    -- ============================================================================
    -- Purpose: Query device statistics, aggregations
    -- Permissions: SELECT only
    CREATE USER IF NOT EXISTS '${MYSQL_STATISTICS_USER}'@'%' IDENTIFIED BY '${MYSQL_STATISTICS_PASSWORD}';
    GRANT SELECT ON ${MYSQL_DATABASE}.* TO '${MYSQL_STATISTICS_USER}'@'%';

    -- Apply all privilege changes
    FLUSH PRIVILEGES;
EOSQL

echo "Database initialization complete!"
echo "Created users:"
echo "  - ${MYSQL_FLYWAY_USER} (full schema management)"
echo "  - ${MYSQL_REGISTRATION_USER} (read/write)"
echo "  - ${MYSQL_STATISTICS_USER} (read-only)"
