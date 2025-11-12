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

## Local Development

Run with dev profile (enables DEBUG logging):
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Development Guidelines

- **No pull requests initially** - POC mode, direct commits to main branch
- **Unit tests mandatory** - All code must include unit tests
- **Automated builds** - CI/CD pipeline using GitHub Workflows, nightly builds for security checks
- **Code quality** - Quality gates and test coverage via SonarCloud
- **Security** - Use an opensource vulnerability checker (probably org.owasp dependency-check-maven, need to check API key requirement)
- **Database migrations** - Use something like Flyway (do it later if enough time)
