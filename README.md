# Device Manager - DevSecOps Interview Task

[![CI](https://github.com/yoda-jm/devices-manager/actions/workflows/ci.yml/badge.svg)](https://github.com/yoda-jm/devices-manager/actions/workflows/ci.yml)
[![Docker Publish](https://github.com/yoda-jm/devices-manager/actions/workflows/docker-publish.yml/badge.svg)](https://github.com/yoda-jm/devices-manager/actions/workflows/docker-publish.yml)
[![SonarQube](https://github.com/yoda-jm/devices-manager/actions/workflows/build.yml/badge.svg)](https://github.com/yoda-jm/devices-manager/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=yoda-jm_device-manager-devsecops-interview-task&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=yoda-jm_device-manager-devsecops-interview-task)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=yoda-jm_device-manager-devsecops-interview-task&metric=coverage)](https://sonarcloud.io/summary/new_code?id=yoda-jm_device-manager-devsecops-interview-task)

## Docker Images

**Statistics API**
[![Docker Pulls](https://img.shields.io/docker/pulls/vincentleligeour/devices-manager-statistics-api?label=pulls)](https://hub.docker.com/r/vincentleligeour/devices-manager-statistics-api)
[![Docker Image Size](https://img.shields.io/docker/image-size/vincentleligeour/devices-manager-statistics-api?label=size)](https://hub.docker.com/r/vincentleligeour/devices-manager-statistics-api)

**Device Registration API**
[![Docker Pulls](https://img.shields.io/docker/pulls/vincentleligeour/devices-manager-device-registration-api?label=pulls)](https://hub.docker.com/r/vincentleligeour/devices-manager-device-registration-api)
[![Docker Image Size](https://img.shields.io/docker/image-size/vincentleligeour/devices-manager-device-registration-api?label=size)](https://hub.docker.com/r/vincentleligeour/devices-manager-device-registration-api)

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
    docker-compose.yml                     # Local development (builds from source)
    docker-compose.prod.yml                # Production (uses Docker Hub images)
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

### Option 1: Local Development (builds from source)

**Build JARs locally (required before Docker build)**
```bash
mvn clean package -DskipTests
```

**Start all services (MariaDB + APIs)**
```bash
docker compose -f docker/docker-compose.yml up -d --build
```

**Note**: Docker images are built from pre-compiled JARs to avoid veth kernel module requirements during build.

### Option 2: Production Deployment (uses Docker Hub images)

**Start all services using pre-built images from Docker Hub**
```bash
docker compose -f docker/docker-compose.prod.yml up -d
```

**Note**: No Maven build required! This pulls the latest published images from `vincentleligeour/devices-manager-*` on Docker Hub.

### Reset database (clean start)
The MariaDB init scripts (`docker/mariadb-init/`) only run on **first startup** with an empty database. To reset and re-run initialization:

```bash
docker volume rm docker_mariadb_data
```

**Warning**: This deletes ALL data in the database. Use for development/testing only.

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

### Run Cluster
```bash
mvn clean package && docker compose -f docker/docker-compose.yml up --build
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

**Note**: Device Registration API requires API key authentication via `X-Device-Registration-API-Key` header.

**POST /Device/register** - Register a device
```bash
curl -X POST http://localhost:8081/Device/register \
  -H "X-Device-Registration-API-Key: 00000000-0000-0000-0000-000000000000" \
  -H "Content-Type: application/json" \
  -d '{"deviceID":"device456","deviceType":"Android"}'
```

**API Key Configuration**:
- Development: `00000000-0000-0000-0000-000000000000` (configured in application-dev.properties)
- Docker: Set `DEVICE_REGISTRATION_API_KEY` in `docker/.env`
- Without valid API key: Returns 401 Unauthorized (no API key) or 403 Forbidden (wrong API key)

## CI/CD & Quality Monitoring

### GitHub Actions
The project uses GitHub Actions for continuous integration and code quality analysis.

**Workflows:**
- **[CI Workflow](https://github.com/yoda-jm/devices-manager/actions/workflows/ci.yml)** - Runs on every push and PR to main branch
  - Compiles source code
  - Compiles tests
  - Runs all unit tests
  - Generates test reports

- **[SonarQube Workflow](https://github.com/yoda-jm/devices-manager/actions/workflows/build.yml)** - Code quality and security analysis
  - Runs build and tests with coverage
  - Executes SonarQube analysis
  - Uploads results to SonarCloud

**View all workflow runs:** [GitHub Actions](https://github.com/yoda-jm/devices-manager/actions)

### SonarCloud Code Quality
Code quality metrics, test coverage, and security analysis are tracked in SonarCloud.

**Project Dashboard:** [SonarCloud - Device Manager](https://sonarcloud.io/project/overview?id=yoda-jm_device-manager-devsecops-interview-task)

**Metrics tracked:**
- Code coverage via JaCoCo
- Code smells and technical debt
- Security vulnerabilities
- Code duplications
- Maintainability ratings

### Running Tests Locally
```bash
# Run all tests
mvn test

# Run tests with coverage
mvn verify

# View coverage report
open target/site/jacoco-aggregate/index.html
```

## Development Guidelines

- **No pull requests initially** - POC mode, direct commits to main branch
- **Unit tests mandatory** - All code must include unit tests
- **Automated builds** - CI/CD pipeline using GitHub Workflows, nightly builds for security checks
- **Code quality** - Quality gates and test coverage via SonarCloud
- **Security** - Use an opensource vulnerability checker (probably org.owasp dependency-check-maven, need to check API key requirement)
- **Database migrations** - Use something like Flyway (do it later if enough time)
