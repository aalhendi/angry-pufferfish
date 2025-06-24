package com.aalhendi.customer_ms.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for JpaCustomerRepository.
 * Uses TestContainers to spin up PostgreSQL instances for each test.
 */
@DataJpaTest
@Import(PostgreSQLTestContainer.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.liquibase.enabled=false"
})
class JpaCustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JpaCustomerRepository repository;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        entityManager.getEntityManager().createQuery("DELETE FROM CustomerEntity").executeUpdate();
        entityManager.flush();

        // Create test data with different customer types and statuses
        LocalDateTime now = LocalDateTime.now();

        // Customer number
        // Name
        // National ID (12 chars)
        // Customer type
        // Address
        // ACTIVE status
        CustomerEntity testCustomer1 = new CustomerEntity(
                null,
                "1234567",          // Customer number
                "John Doe",         // Name
                "325010179353",     // National ID (12 chars)
                "RETAIL",           // Customer type
                "OMAR BEN AL KHATTAB STREET, ARRAYA TOWER, FLOOR 01-13, BLOCK 7, SHARQ", // Address
                1,                  // ACTIVE status
                now,
                now
        );

        // Customer number
        // Name
        // National ID (12 chars)
        // Customer type
        // Address
        // INACTIVE status
        CustomerEntity testCustomer2 = new CustomerEntity(
                null,
                "7654321",          // Customer number
                "Jane Smith",       // Name
                "325010160759",     // National ID (12 chars)
                "RETAIL",           // Customer type
                "OMAR BEN AL KHATTAB STREET, AVENUES MALL, FLOOR 01-13, BLOCK 7, SHARQ",  // Address
                0,                  // INACTIVE status
                now,
                now
        );

        // Customer number
        // Name
        // National ID (12 chars)
        // Customer type
        // Address
        // ACTIVE status
        CustomerEntity testCustomer3 = new CustomerEntity(
                null,
                "9876543",          // Customer number
                "W BANK",           // Name
                "325010150000",     // National ID (12 chars)
                "CORPORATE",        // Customer type
                "OMAR BEN AL KHATTAB STREET, ARRAYA COMPLEX, FLOOR 01-13, BLOCK 7, SHARQ",              // Address
                1,                  // ACTIVE status
                now,
                now
        );

        // Persist test data
        entityManager.persistAndFlush(testCustomer1);
        entityManager.persistAndFlush(testCustomer2);
        entityManager.persistAndFlush(testCustomer3);
    }

    @Test
    void shouldFindCustomerByCustomerNumber() {
        // When - finding customer by customer number
        Optional<CustomerEntity> customer = repository.findByCustomerNumber("1234567");

        // Then - should find the customer
        assertTrue(customer.isPresent());
        assertEquals("1234567", customer.get().getCustomerNumber());
        assertEquals("John Doe", customer.get().getName());
        assertEquals("325010179353", customer.get().getNationalId());
    }

    @Test
    void shouldNotFindNonExistentCustomer() {
        // When - searching for a non-existent customer
        Optional<CustomerEntity> customer = repository.findByCustomerNumber("9999999");

        // Then - should return empty
        assertFalse(customer.isPresent());
    }

    @Test
    void shouldFindCustomerByNationalId() {
        // When - finding customer by national ID
        Optional<CustomerEntity> customer = repository.findByNationalId("325010179353");

        // Then - should find the customer
        assertTrue(customer.isPresent());
        assertEquals("John Doe", customer.get().getName());
        assertEquals("1234567", customer.get().getCustomerNumber());
    }

    @Test
    void shouldCheckExistenceByCustomerNumber() {
        // When - checking if a customer exists
        boolean exists = repository.existsByCustomerNumber("1234567");
        boolean notExists = repository.existsByCustomerNumber("9999999");

        // Then - should return correct existence
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void shouldCheckExistenceByNationalId() {
        // When - checking if a customer exists by national ID
        boolean exists = repository.existsByNationalId("325010179353");
        boolean notExists = repository.existsByNationalId("999999999999");

        // Then - should return correct existence
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void shouldFindCustomersByStatus() {
        // When - finding customers by status
        List<CustomerEntity> activeCustomers = repository.findByStatus(1);
        List<CustomerEntity> inactiveCustomers = repository.findByStatus(0);

        // Then - should return customers with correct status
        assertEquals(2, activeCustomers.size()); // testCustomer1 and testCustomer3 are active
        assertEquals(1, inactiveCustomers.size()); // testCustomer2 is inactive

        assertTrue(activeCustomers.stream().allMatch(c -> c.getStatus() == 1));
        assertTrue(inactiveCustomers.stream().allMatch(c -> c.getStatus() == 0));
    }

    @Test
    void shouldFindCustomersByCustomerType() {
        // When - finding customers by type
        List<CustomerEntity> retailCustomers = repository.findByCustomerType("RETAIL");
        List<CustomerEntity> corporateCustomers = repository.findByCustomerType("CORPORATE");

        // Then - should return customers with correct type
        assertEquals(2, retailCustomers.size()); // testCustomer1 and testCustomer2 are retail
        assertEquals(1, corporateCustomers.size()); // testCustomer3 is corporate

        assertTrue(retailCustomers.stream().allMatch(c -> "RETAIL".equals(c.getCustomerType())));
        assertTrue(corporateCustomers.stream().allMatch(c -> "CORPORATE".equals(c.getCustomerType())));
    }

    @Test
    void shouldCountCustomersByType() {
        // When - counting customers by type
        long retailCount = repository.countByCustomerType("RETAIL");
        long corporateCount = repository.countByCustomerType("CORPORATE");
        long investmentCount = repository.countByCustomerType("INVESTMENT");

        // Then - should return correct counts
        assertEquals(2, retailCount);
        assertEquals(1, corporateCount);
        assertEquals(0, investmentCount); // No investment customers in test data
    }

    @Test
    void shouldFindCustomersCreatedAfterDate() {
        // Given - a cutoff date in the past
        LocalDateTime cutoffDate = LocalDateTime.now().minusHours(1);

        // When - finding customers created after cutoff
        List<CustomerEntity> recentCustomers = repository.findByCreatedAtAfter(cutoffDate);

        // Then - should find all customers (all created recently)
        assertEquals(3, recentCustomers.size());
    }

    @Test
    void shouldSearchCustomersByNameContaining() {
        // When - searching customers by name fragment
        List<CustomerEntity> johnsResults = repository.findByNameContainingIgnoreCase("john");
        List<CustomerEntity> corpResults = repository.findByNameContainingIgnoreCase("bank");
        List<CustomerEntity> emptyResults = repository.findByNameContainingIgnoreCase("xyz");

        // Then - should return matching customers
        assertEquals(1, johnsResults.size());
        assertEquals("John Doe", johnsResults.getFirst().getName());

        assertEquals(1, corpResults.size());
        assertEquals("W BANK", corpResults.getFirst().getName());

        assertEquals(0, emptyResults.size());
    }
} 