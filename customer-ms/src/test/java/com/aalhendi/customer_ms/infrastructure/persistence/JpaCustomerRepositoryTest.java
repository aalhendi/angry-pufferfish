package com.aalhendi.customer_ms.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for JpaCustomerRepository.
 */
@DataJpaTest
@ActiveProfiles("test")
class JpaCustomerRepositoryTest {

    // Repository will be injected through TDD
    private JpaCustomerRepository jpaCustomerRepository;

    @Test
    void shouldFindCustomerByCustomerNumber() {
        // Given - saved customer entity
        CustomerEntity customer = createTestCustomer("1234567", "John Doe", "325010179353");
        jpaCustomerRepository.save(customer);

        // When - finding by customer number
        Optional<CustomerEntity> found = jpaCustomerRepository.findByCustomerNumber("1234567");

        // Then - should find the customer
        assertTrue(found.isPresent());
        assertEquals("1234567", found.get().getCustomerNumber());
        assertEquals("John Doe", found.get().getName());
    }

    @Test
    void shouldNotFindNonExistentCustomer() {
        // When - searching for non-existent customer
        Optional<CustomerEntity> found = jpaCustomerRepository.findByCustomerNumber("9999999");

        // Then - should not find anything
        assertFalse(found.isPresent());
    }

    @Test
    void shouldFindCustomerByNationalId() {
        // Given - saved customer entity
        CustomerEntity customer = createTestCustomer("1234567", "John Doe", "325010179353");
        jpaCustomerRepository.save(customer);

        // When - finding by national ID
        Optional<CustomerEntity> found = jpaCustomerRepository.findByNationalId("325010179353");

        // Then - should find the customer
        assertTrue(found.isPresent());
        assertEquals("325010179353", found.get().getNationalId());
        assertEquals("John Doe", found.get().getName());
    }

    @Test
    void shouldFindActiveCustomers() {
        // Given - mix of active and inactive customers
        CustomerEntity activeCustomer = createTestCustomer("1234567", "John Doe", "325010179353");
        activeCustomer.setStatus(1); // ACTIVE
        
        CustomerEntity inactiveCustomer = createTestCustomer("7654321", "Jane Smith", "325010160759");
        inactiveCustomer.setStatus(0); // INACTIVE
        
        jpaCustomerRepository.save(activeCustomer);
        jpaCustomerRepository.save(inactiveCustomer);

        // When - finding active customers only
        List<CustomerEntity> activeCustomers = jpaCustomerRepository.findByStatus(1);

        // Then - should find only active customers
        assertEquals(1, activeCustomers.size());
        assertEquals("John Doe", activeCustomers.get(0).getName());
        assertEquals(1, activeCustomers.get(0).getStatus());
    }

    @Test
    void shouldFindCustomersByType() {
        // Given - customers of different types
        CustomerEntity retailCustomer = createTestCustomer("1234567", "John Doe", "325010179353");
        retailCustomer.setCustomerType("RETAIL");
        
        CustomerEntity corporateCustomer = createTestCustomer("7654321", "ABC Corp", "325010160759");
        corporateCustomer.setCustomerType("CORPORATE");
        
        jpaCustomerRepository.save(retailCustomer);
        jpaCustomerRepository.save(corporateCustomer);

        // When - finding retail customers
        List<CustomerEntity> retailCustomers = jpaCustomerRepository.findByCustomerType("RETAIL");

        // Then - should find only retail customers
        assertEquals(1, retailCustomers.size());
        assertEquals("John Doe", retailCustomers.get(0).getName());
        assertEquals("RETAIL", retailCustomers.get(0).getCustomerType());
    }

    @Test
    void shouldCheckExistenceByCustomerNumber() {
        // Given - saved customer
        jpaCustomerRepository.save(createTestCustomer("1234567", "John Doe", "325010179353"));

        // When - checking existence
        boolean exists = jpaCustomerRepository.existsByCustomerNumber("1234567");
        boolean notExists = jpaCustomerRepository.existsByCustomerNumber("9999999");

        // Then - should return correct existence status
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void shouldCheckExistenceByNationalId() {
        // Given - saved customer
        jpaCustomerRepository.save(createTestCustomer("1234567", "John Doe", "325010179353"));

        // When - checking existence
        boolean exists = jpaCustomerRepository.existsByNationalId("325010179353");
        boolean notExists = jpaCustomerRepository.existsByNationalId("999999999999");

        // Then - should return correct existence status
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void shouldCountCustomersByType() {
        // Given - customers of different types
        jpaCustomerRepository.save(createTestCustomer("1234567", "John Doe", "325010179353", "RETAIL"));
        jpaCustomerRepository.save(createTestCustomer("7654321", "Jane Smith", "325010160759", "RETAIL"));
        jpaCustomerRepository.save(createTestCustomer("1111111", "ABC Corp", "325010150000", "CORPORATE"));

        // When - counting retail customers
        long retailCount = jpaCustomerRepository.countByCustomerType("RETAIL");
        long corporateCount = jpaCustomerRepository.countByCustomerType("CORPORATE");

        // Then - should return correct counts
        assertEquals(2, retailCount);
        assertEquals(1, corporateCount);
    }

    @Test
    void shouldFindCustomersCreatedAfterDate() {
        // Given - customers created at different times
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(1);
        
        CustomerEntity oldCustomer = createTestCustomer("1234567", "John Doe", "325010179353");
        oldCustomer.setCreatedAt(cutoffDate.minusHours(1)); // Before cutoff
        
        CustomerEntity newCustomer = createTestCustomer("7654321", "Jane Smith", "325010160759");
        newCustomer.setCreatedAt(cutoffDate.plusHours(1)); // After cutoff
        
        jpaCustomerRepository.save(oldCustomer);
        jpaCustomerRepository.save(newCustomer);

        // When - finding customers created after cutoff
        List<CustomerEntity> recentCustomers = jpaCustomerRepository.findByCreatedAtAfter(cutoffDate);

        // Then - should find only recent customers
        assertEquals(1, recentCustomers.size());
        assertEquals("Jane Smith", recentCustomers.get(0).getName());
    }

    private CustomerEntity createTestCustomer(String customerNumber, String name, String nationalId) {
        return createTestCustomer(customerNumber, name, nationalId, "RETAIL");
    }

    private CustomerEntity createTestCustomer(String customerNumber, String name, String nationalId, String customerType) {
        CustomerEntity customer = new CustomerEntity();
        customer.setCustomerNumber(customerNumber);
        customer.setName(name);
        customer.setNationalId(nationalId);
        customer.setCustomerType(customerType);
        customer.setAddress("OMAR BEN AL KHATTAB STREET, ARRAYA TOWER, FLOOR 01-13, BLOCK 7, SHARQ");
        customer.setStatus(1); // ACTIVE by default
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        return customer;
    }
} 