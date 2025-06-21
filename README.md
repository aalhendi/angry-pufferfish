# Angry Pufferfish - Microservice Demo

## Overview

- account microservice on 8080
- customer microservice on 8081
- PostgreSQL instance on 5432 with 2 dbs (one for each service)

Db migrations are managed through Liquibase.

### Prerequisites

- Java 21+ TODO(aalhendi): dockerize this as well? just a one line runner
- Docker

### 1. Start Database

```bash
docker-compose up -d
```

### 2. Start Microservices

**Account Microservice:**

```bash
cd account-ms
./mvnw spring-boot:run
```

**Customer Microservice:**

```bash
cd customer-ms  
./mvnw spring-boot:run
```

### 3. Verify Setup

- Account MS Health: http://localhost:8080/actuator/health (/info, /metrics)
- Liquibase Status: http://localhost:8080/actuator/liquibase

## TODO(aalhendi): discuss DB schema here? or somewhere else?

## Configuration

### Database

- **Host**: localhost:5432
- **Admin User**: admin / admin123
- **Account DB**: account_db / account_user / account_pass
- **Customer DB**: customer_db / customer_user / customer_pass
