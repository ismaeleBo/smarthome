# Smart Home - Distributed Systems Engineering Project

This repository contains a **Smart Home** management system developed as an educational project for the Distributed Systems Engineering course.  
It was developed as a project within of the Master's Degree in Computer Science LM-18 at the University of Catania.

## Project Overview

The system is built using a **Microservices Architecture**, focusing on scalability, event-driven communication, and separation of concerns. It allows users to manage their homes, track sensor measurements, and analyze data through a dedicated dashboard.

### Core Features

- **User Authentication & Authorization**: RBAC (Role-Based Access Control) with JWT.
- **Home Management**: Create homes, invite members, and manage roles.
- **Measurement Tracking**: Real-time or batch sensor data collection.
- **Analytics Dashboard**: Advanced data visualization and performance metrics.
- **Notifications**: Event-driven email notifications for invitations and account activations.
- **API Gateway**: Unified entry point for all microservices.

## Tech Stack

### Backend

- **Java 21** with **Spring Boot 4.x** (Experimental/Latest)
- **Spring Security** (JWT Authentication)
- **Spring Data JPA** & **Flyway** (Database Migrations)
- **RabbitMQ** (Message Broker for Event-Driven Communication)
- **Spring Cloud Gateway** & **Resilience4j** (API Gateway & Circuit Breaker)
- **OpenAPI / Swagger** (Interactive API Documentation)

### Frontend

- **SvelteKit 5** (TypeScript)
- **Tailwind CSS** (Styling)
- **Chart.js** (Analytics Visualization)

### Infrastructure

- **Docker & Docker Compose** (Containerization)
- **PostgreSQL 16** (Relational Database)
- **Mailtrap** (SMTP server testing for notifications)

## Project Structure

```text
.
├── analytics-service/      # Data analysis and metrics calculation
├── api-gateway/            # Unified entry point and request routing
├── auth-service/           # User identity, JWT issuance, and RBAC
├── home-service/           # Home membership management
├── measurements-service/   # Sensor data collection and storage
├── notification-service/   # Email delivery based on RabbitMQ events
├── frontend/               # SvelteKit web application
├── diagrams/               # Architectural diagrams
├── infra/                  # Postgres init
└── docker-compose.yml      # Orchestration for all services
```

## Setup and Installation

### Prerequisites

- [Docker](https://www.docker.com/) & Docker Compose
- [Node.js](https://nodejs.org/) (optional, for local frontend dev)
- [Java 21](https://www.oracle.com/java/technologies/downloads/) (optional, for local backend dev)

### 1. Configuration

Create a `.env` file in the root directory by copying the example provided:

```bash
cp env.example .env
```

Edit the `.env` file and fill in the required values (JWT secrets, DB credentials, Mailtrap credentials, etc.).

### 2. Launch the System

Run the following command to build and start all containers:

```bash
docker-compose up --build
```

This will start the databases, RabbitMQ, all microservices, and the frontend.

## Usage

### Web Interface

Once the containers are running, you can access the frontend at:
**[http://localhost:5173](http://localhost:5173)**

### Testing APIs (Swagger UI)

Each microservice exposes its own Swagger documentation. You can test the endpoints directly from your browser:

- **Auth Service**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **Measurements Service**: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)
- **Home Service**: [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html)
- **Analytics Service**: [http://localhost:8083/swagger-ui/index.html](http://localhost:8083/swagger-ui/index.html)
- **API Gateway**: [http://localhost:8085/swagger-ui/index.html](http://localhost:8085/swagger-ui/index.html)

### Monitoring

- **RabbitMQ Management**: [http://localhost:15672](http://localhost:15672) (User/Pass as configured in `.env`)
