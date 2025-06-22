package com.aalhendi.customer_ms.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CustomerEntity JPA mapping.
 */
class CustomerEntityTest {

    private CustomerEntity customerEntity;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        testTime = LocalDateTime.now();
        customerEntity = new CustomerEntity();
    }

    @Test
    void shouldCreateCustomerEntityWithBasicProperties() {
        // Given - basic customer data
        String expectedCustomerNumber = "1234567";
        String expectedName = "John Doe";
        String expectedNationalId = "325010179353";
        String expectedCustomerType = "RETAIL";
        String expectedAddress = "OMAR BEN AL KHATTAB STREET, ARRAYA TOWER, FLOOR 01-13, BLOCK 7, SHARQ";
        Integer expectedStatus = 1;

        // When - setting properties
        customerEntity.setCustomerNumber(expectedCustomerNumber);
        customerEntity.setName(expectedName);
        customerEntity.setNationalId(expectedNationalId);
        customerEntity.setCustomerType(expectedCustomerType);
        customerEntity.setAddress(expectedAddress);
        customerEntity.setStatus(expectedStatus);
        customerEntity.setCreatedAt(testTime);
        customerEntity.setUpdatedAt(testTime);

        // Then - properties should be retrievable
        assertEquals(expectedCustomerNumber, customerEntity.getCustomerNumber());
        assertEquals(expectedName, customerEntity.getName());
        assertEquals(expectedNationalId, customerEntity.getNationalId());
        assertEquals(expectedCustomerType, customerEntity.getCustomerType());
        assertEquals(expectedAddress, customerEntity.getAddress());
        assertEquals(expectedStatus, customerEntity.getStatus());
        assertEquals(testTime, customerEntity.getCreatedAt());
        assertEquals(testTime, customerEntity.getUpdatedAt());
    }

    @Test
    void shouldValidateNationalIdFormat() {
        // Given - valid national ID
        customerEntity.setNationalId("325010179353");

        // When - validating format
        boolean isValid = customerEntity.isValidNationalIdFormat();

        // Then - should be valid
        assertTrue(isValid);
    }

    @Test
    void shouldRejectInvalidNationalIdFormat() {
        // Given - invalid national ID (too short)
        customerEntity.setNationalId("32501017935");

        // When - validating format
        boolean isValid = customerEntity.isValidNationalIdFormat();

        // Then - should be invalid
        assertFalse(isValid);
    }

    @Test
    void shouldCheckIfCustomerIsActive() {
        // Given - active customer
        customerEntity.setStatus(1); // ACTIVE

        // When - checking if active
        boolean isActive = customerEntity.isActive();

        // Then - should be active
        assertTrue(isActive);
    }

    @Test
    void shouldCheckIfCustomerIsInactive() {
        // Given - inactive customer
        customerEntity.setStatus(0); // INACTIVE

        // When - checking if active
        boolean isActive = customerEntity.isActive();

        // Then - should not be active
        assertFalse(isActive);
    }

    @Test
    void shouldImplementEqualsBasedOnCustomerNumber() {
        // Given - two entities with same customer number
        CustomerEntity entity1 = new CustomerEntity();
        entity1.setCustomerNumber("1234567");
        
        CustomerEntity entity2 = new CustomerEntity();
        entity2.setCustomerNumber("1234567");

        CustomerEntity entity3 = new CustomerEntity();
        entity3.setCustomerNumber("7654321");

        // Then - equals should be based on customer number
        assertEquals(entity1, entity2);
        assertNotEquals(entity1, entity3);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void shouldProvideToStringRepresentation() {
        // Given - entity with data
        customerEntity.setCustomerNumber("1234567");
        customerEntity.setName("John Doe");
        customerEntity.setCustomerType("RETAIL");

        // When - calling toString
        String stringRepresentation = customerEntity.toString();

        // Then - should contain key information
        assertNotNull(stringRepresentation);
        assertTrue(stringRepresentation.contains("1234567"));
        assertTrue(stringRepresentation.contains("John Doe"));
        assertTrue(stringRepresentation.contains("CustomerEntity"));
    }

    @Test
    void shouldCreateEntityWithParameterizedConstructor() {
        // Given - constructor parameters
        String customerNumber = "1234567";
        String name = "Jane Smith";
        String nationalId = "325010160759";
        String customerType = "CORPORATE";
        String address = "OMAR BEN AL KHATTAB STREET, ARRAYA TOWER, FLOOR 01-13, BLOCK 7, SHARQ";
        Integer status = 1;

        // When - creating entity with constructor
        CustomerEntity entity = new CustomerEntity(customerNumber, name, nationalId, 
            customerType, address, status, testTime, testTime, null);

        // Then - properties should be set correctly
        assertEquals(customerNumber, entity.getCustomerNumber());
        assertEquals(name, entity.getName());
        assertEquals(nationalId, entity.getNationalId());
        assertEquals(customerType, entity.getCustomerType());
        assertEquals(address, entity.getAddress());
        assertEquals(status, entity.getStatus());
        assertEquals(testTime, entity.getCreatedAt());
        assertEquals(testTime, entity.getUpdatedAt());
        assertNull(entity.getDeletedAt());
    }
} 