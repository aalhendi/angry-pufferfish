package com.aalhendi.customer_ms.infrastructure.persistence;

import com.aalhendi.customer_ms.domain.entities.Customer;
import com.aalhendi.customer_ms.domain.entities.NewCustomer;
import com.aalhendi.customer_ms.domain.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CustomerEntity JPA entity.
 */
class CustomerEntityTest {

    private CustomerEntity customerEntity;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        testTime = LocalDateTime.now().minusSeconds(5);
        customerEntity = new CustomerEntity();
        customerEntity.setId(1L);
        customerEntity.setCustomerNumber("1234567");
        customerEntity.setName("John Doe");
        customerEntity.setNationalId("123456789012");
        customerEntity.setCustomerType("RETAIL");
        customerEntity.setAddress("OMAR BEN AL KHATTAB STREET, ARRAYA TOWER, FLOOR 01-13, BLOCK 7, SHARQ");
        customerEntity.setStatus(1);
        customerEntity.setCreatedAt(testTime);
        customerEntity.setUpdatedAt(testTime);
    }

    @Test
    @DisplayName("should get and set ID correctly")
    void shouldGetAndSetIdCorrectly() {
        // Given
        Long expectedId = 42L;

        // When
        customerEntity.setId(expectedId);

        // Then
        assertEquals(expectedId, customerEntity.getId());
    }

    @Test
    @DisplayName("should get and set customer number correctly")
    void shouldGetAndSetCustomerNumberCorrectly() {
        // Given
        String expectedCustomerNumber = "7654321";

        // When
        customerEntity.setCustomerNumber(expectedCustomerNumber);

        // Then
        assertEquals(expectedCustomerNumber, customerEntity.getCustomerNumber());
    }

    @Test
    @DisplayName("should get and set name correctly")
    void shouldGetAndSetNameCorrectly() {
        // Given
        String expectedName = "Jane Smith";

        // When
        customerEntity.setName(expectedName);

        // Then
        assertEquals(expectedName, customerEntity.getName());
    }

    @Test
    @DisplayName("should validate national ID format correctly")
    void shouldValidateNationalIdFormatCorrectly() {
        // Valid format tests
        customerEntity.setNationalId("123456789012");
        assertTrue(customerEntity.isValidNationalIdFormat());

        customerEntity.setNationalId("456789012345");
        assertTrue(customerEntity.isValidNationalIdFormat());

        // Invalid format tests
        customerEntity.setNationalId("12345678901"); // too short
        assertFalse(customerEntity.isValidNationalIdFormat());

        customerEntity.setNationalId("1234567890123"); // too long
        assertFalse(customerEntity.isValidNationalIdFormat());

        customerEntity.setNationalId("12345678901a"); // contains letter
        assertFalse(customerEntity.isValidNationalIdFormat());

        customerEntity.setNationalId("012345678901"); // starts with 0
        assertFalse(customerEntity.isValidNationalIdFormat());

        customerEntity.setNationalId("512345678901"); // starts with 5
        assertFalse(customerEntity.isValidNationalIdFormat());

        customerEntity.setNationalId(null); // null
        assertFalse(customerEntity.isValidNationalIdFormat());
    }

    @Test
    @DisplayName("should check if customer is active correctly")
    void shouldCheckIfCustomerIsActiveCorrectly() {
        // Active status
        customerEntity.setStatus(1);
        assertTrue(customerEntity.isActive());

        // Inactive statuses
        customerEntity.setStatus(0);
        assertFalse(customerEntity.isActive());

        customerEntity.setStatus(2);
        assertFalse(customerEntity.isActive());

        customerEntity.setStatus(null);
        assertFalse(customerEntity.isActive());
    }

    @Test
    @DisplayName("should update timestamp when touched")
    void shouldUpdateTimestampWhenTouched() {
        // Given
        LocalDateTime originalTime = customerEntity.getUpdatedAt();

        // When
        customerEntity.touch();

        // Then
        assertNotEquals(originalTime, customerEntity.getUpdatedAt());
        assertTrue(customerEntity.getUpdatedAt().isAfter(originalTime));
    }

    @Test
    @DisplayName("should create entity with all constructor parameters")
    void shouldCreateEntityWithAllConstructorParameters() {
        // Given
        String customerNumber = "9876543";
        String name = "John Doe";
        String nationalId = "210987654321";
        String customerType = "INDIVIDUAL";
        String address = "OMAR BEN AL KHATTAB STREET, ARRAYA TOWER, FLOOR 01-13, BLOCK 7, SHARQ";
        Integer status = 1;

        // When creating an entity with constructor
        CustomerEntity entity = new CustomerEntity(null, customerNumber, name, nationalId,
            customerType, address, status, testTime, testTime);

        // Then - properties should be set correctly
        assertEquals(customerNumber, entity.getCustomerNumber());
        assertEquals(name, entity.getName());
        assertEquals(nationalId, entity.getNationalId());
        assertEquals(customerType, entity.getCustomerType());
        assertEquals(address, entity.getAddress());
        assertEquals(status, entity.getStatus());
        assertEquals(testTime, entity.getCreatedAt());
        assertEquals(testTime, entity.getUpdatedAt());
    }

    @Test
    @DisplayName("should convert JPA entity to domain Customer object")
    void shouldConvertJpaEntityToDomainCustomerObject() {
        // When
        Customer domainCustomer = customerEntity.toDomain();

        // Then
        assertNotNull(domainCustomer);
        assertEquals(customerEntity.getId(), domainCustomer.getId());
        assertEquals(customerEntity.getCustomerNumber(), domainCustomer.getCustomerNumber().value());
        assertEquals(customerEntity.getName(), domainCustomer.getName().value());
        assertEquals(customerEntity.getNationalId(), domainCustomer.getNationalId().value());
        assertEquals(customerEntity.getCustomerType(), domainCustomer.getCustomerType().name());
        assertEquals(customerEntity.getAddress(), domainCustomer.getAddress().value());
        assertEquals(customerEntity.getStatus(), domainCustomer.getStatus().getCode());
        assertEquals(customerEntity.getCreatedAt(), domainCustomer.getCreatedAt());
        assertEquals(customerEntity.getUpdatedAt(), domainCustomer.getUpdatedAt());
    }

    @Test
    @DisplayName("should create JPA entity from domain Customer object")
    void shouldCreateJpaEntityFromDomainCustomerObject() {
        // Given
        Customer domainCustomer = Customer.reconstitute(
            1L,
            new CustomerNumber("1234567"),
            new CustomerName("John Doe"),
            new NationalId("123456789012"),
            CustomerType.RETAIL,
            new Address("OMAR BEN AL KHATTAB STREET, ARRAYA TOWER, FLOOR 01-13, BLOCK 7, SHARQ"),
            CustomerStatus.ACTIVE,
            testTime,
            testTime
        );

        // When
        CustomerEntity entity = CustomerEntity.fromDomain(domainCustomer);

        // Then
        assertNotNull(entity);
        assertEquals(domainCustomer.getId(), entity.getId());
        assertEquals(domainCustomer.getCustomerNumber().value(), entity.getCustomerNumber());
        assertEquals(domainCustomer.getName().value(), entity.getName());
        assertEquals(domainCustomer.getNationalId().value(), entity.getNationalId());
        assertEquals(domainCustomer.getCustomerType().name(), entity.getCustomerType());
        assertEquals(domainCustomer.getAddress().value(), entity.getAddress());
        assertEquals(domainCustomer.getStatus().getCode(), entity.getStatus());
        assertEquals(domainCustomer.getCreatedAt(), entity.getCreatedAt());
        assertEquals(domainCustomer.getUpdatedAt(), entity.getUpdatedAt());
    }

    @Test
    @DisplayName("should create JPA entity from domain NewCustomer object")
    void shouldCreateJpaEntityFromDomainNewCustomerObject() {
        // Given
        NewCustomer newCustomer = NewCustomer.create(
            new CustomerNumber("7654321"),
            new CustomerName("Jane Smith"),
            new NationalId("210987654321"),
            CustomerType.CORPORATE,
            new Address("456 Business Ave, Commercial District")
        );

        // When
        CustomerEntity entity = CustomerEntity.fromDomain(newCustomer);

        // Then
        assertNotNull(entity);
        assertNull(entity.getId()); // NewCustomer doesn't have an ID
        assertEquals(newCustomer.getCustomerNumber().value(), entity.getCustomerNumber());
        assertEquals(newCustomer.getName().value(), entity.getName());
        assertEquals(newCustomer.getNationalId().value(), entity.getNationalId());
        assertEquals(newCustomer.getCustomerType().name(), entity.getCustomerType());
        assertEquals(newCustomer.getAddress().value(), entity.getAddress());
        assertEquals(newCustomer.getStatus().getCode(), entity.getStatus());
        assertEquals(newCustomer.getCreatedAt(), entity.getCreatedAt());
        assertEquals(newCustomer.getUpdatedAt(), entity.getUpdatedAt());
    }

    @Test
    @DisplayName("should handle round-trip conversion (entity -> domain -> entity)")
    void shouldHandleRoundTripConversion() {
        // Given - start with a JPA entity
        CustomerEntity originalEntity = customerEntity;

        // When - convert to domain and back to entity
        Customer domainCustomer = originalEntity.toDomain();
        CustomerEntity convertedEntity = CustomerEntity.fromDomain(domainCustomer);

        // Then - should be equivalent to the original
        assertEquals(originalEntity.getId(), convertedEntity.getId());
        assertEquals(originalEntity.getCustomerNumber(), convertedEntity.getCustomerNumber());
        assertEquals(originalEntity.getName(), convertedEntity.getName());
        assertEquals(originalEntity.getNationalId(), convertedEntity.getNationalId());
        assertEquals(originalEntity.getCustomerType(), convertedEntity.getCustomerType());
        assertEquals(originalEntity.getAddress(), convertedEntity.getAddress());
        assertEquals(originalEntity.getStatus(), convertedEntity.getStatus());
        assertEquals(originalEntity.getCreatedAt(), convertedEntity.getCreatedAt());
        assertEquals(originalEntity.getUpdatedAt(), convertedEntity.getUpdatedAt());
    }

    @Test
    @DisplayName("should handle domain validation during conversion")
    void shouldHandleDomainValidationDuringConversion() {
        // Given - entity with invalid data
        customerEntity.setCustomerNumber("invalid"); // not 7 digits
        
        // When & Then - conversion should throw a domain validation exception
        assertThrows(IllegalArgumentException.class, () -> {
            customerEntity.toDomain();
        });
    }
} 