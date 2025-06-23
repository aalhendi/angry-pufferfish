package com.aalhendi.customer_ms.infrastructure.persistence;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * TestContainers configuration for PostgreSQL database tests.
 */
@TestConfiguration(proxyBeanMethods = false)
public class PostgreSQLTestContainer {

    /**
     * Creates a PostgreSQL container for testing.
     * Each test gets a fresh database instance for complete isolation.
     */
    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer() {
        // TODO(aalhendi): we can define this once and reuse. right now the pg version is defined in multiple places. same with test credentials.
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:17"))
                .withDatabaseName("customer_test_db")
                .withUsername("test_user")
                .withPassword("test_password")
                .withReuse(false);
    }
} 