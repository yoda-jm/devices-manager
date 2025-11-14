# Device Manager - DevSecOps Interview Task

A device tracking system for identifying the most frequently used device types across multiple platforms.

## Overview

The goal of this project is to implement the specifications given in the [`docs/DevSecOps Interview Task-new.pdf`](docs/DevSecOps%20Interview%20Task-new.pdf) file.

## Architecture

### StatisticsAPI (Public)
- `POST /Log/auth` - Store user login event and forward to DeviceRegistrationAPI
- `GET /Log/auth/statistics` - Retrieve device registration counts by type

### DeviceRegistrationAPI (Internal)
- `POST /Device/register` - Register device type for a given user

## Technical Stack Options

| Component | Available Choices |
|-----------|------------------|
| **Backend Framework** | Node.js, .NET, Python, Java Spring Boot |
| **Database** | PostgreSQL, MongoDB, MariaDB, Redis, Elasticsearch, SQL Server Express, Oracle XE |
| **Container Registry** | DockerHub |
| **Orchestration** | Kubernetes, OpenShift, Docker Compose |

## Selected Technical Stack

| Component | Choice |
|-----------|--------|
| **Backend Framework** | Java Spring Boot 3.5.7 (latest stable) |
| **Database** | MariaDB |
| **Container Registry** | DockerHub |
| **Orchestration** | Docker Compose |
| **Java Version** | 25 (LTS) |
| **Project Structure** | Maven multi-module |

## Project Structure

Multi-module Maven project with separate deployable artifacts:

```
devices-manager/
  pom.xml                                  # Parent POM
  api-specs/                               # OpenAPI 3.1.0 specifications
  docs/                                    # Documentation
  docker/                                  # Docker configuration
    docker-compose.yml                     # Full stack setup
    Dockerfile.statistics-api              # Statistics API container
    Dockerfile.device-registration-api     # Device Registration API container
  services/                                # Maven modules
    common/                                # Shared code
    statistics-api/                        # Statistics API (port 8080)
    device-registration-api/               # Device Registration API (port 8081)
```

## Docker Deployment

### Setup environment variables
First, copy the example environment file and customize if needed:
```bash
cp docker/.env.example docker/.env
```

Edit `docker/.env` to set your database credentials and configuration.

### Build JARs locally (required before Docker build)
```bash
mvn clean package -DskipTests
```

### Start all services (MariaDB + APIs)
```bash
docker compose -f docker/docker-compose.yml up -d --build
```

**Note**: Docker images are built from pre-compiled JARs to avoid veth kernel module requirements during build.

### Database Connection Details
Default values (configurable in `docker/.env`):
- **Host**: localhost
- **Port**: 3306 (`MYSQL_PORT`)
- **Database**: devices_manager (`MYSQL_DATABASE`)
- **User**: devuser (`MYSQL_USER`)
- **Password**: devpass (`MYSQL_PASSWORD`)
- **Root Password**: root (`MYSQL_ROOT_PASSWORD`)

### Services
- **Statistics API**: http://localhost:8080
- **Device Registration API**: http://localhost:8081

**Note**: All services use `network_mode: host` to work around veth kernel module requirements on Gentoo.

## Local Development

### Build all modules (required first)
```bash
mvn clean install
```

### Run Statistics API (port 8080)
```bash
(cd services/statistics-api && mvn spring-boot:run -Dspring-boot.run.profiles=dev)
```

### Run Device Registration API (port 8081)
```bash
(cd services/device-registration-api && mvn spring-boot:run -Dspring-boot.run.profiles=dev)
```

## Testing the APIs

### Statistics API (port 8080)

**GET /Log/auth/statistics** - Retrieve device statistics by type
```bash
curl http://localhost:8080/Log/auth/statistics?deviceType=iOS
```

**POST /Log/auth** - Log authentication event
```bash
curl -X POST http://localhost:8080/Log/auth \
  -H "Content-Type: application/json" \
  -d '{"deviceID":"device123","deviceType":"iOS"}'
```

### Device Registration API (port 8081)

**POST /Device/register** - Register a device
```bash
curl -X POST http://localhost:8081/Device/register \
  -H "Content-Type: application/json" \
  -d '{"deviceID":"device456","deviceType":"Android"}'
```

## Development Guidelines

- **No pull requests initially** - POC mode, direct commits to main branch
- **Unit tests mandatory** - All code must include unit tests
- **Automated builds** - CI/CD pipeline using GitHub Workflows, nightly builds for security checks
- **Code quality** - Quality gates and test coverage via SonarCloud
- **Security** - Use an opensource vulnerability checker (probably org.owasp dependency-check-maven, need to check API key requirement)
- **Database migrations** - Use something like Flyway (do it later if enough time)
