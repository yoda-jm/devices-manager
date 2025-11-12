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
| **Backend Framework** | Java Spring Boot |
| **Database** | MariaDB |
| **Container Registry** | DockerHub |
| **Orchestration** | Docker Compose |

## Development Guidelines

- **No pull requests initially** - POC mode, direct commits to main branch
- **Unit tests mandatory** - All code must include unit tests
- **Automated builds** - CI/CD pipeline using GitHub Workflows
- **Code quality** - Quality gates and test coverage via SonarCloud
