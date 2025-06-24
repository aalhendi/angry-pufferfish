package com.aalhendi.account_ms.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for JpaAccountRepository.
 * Uses TestContainers to spin up PostgreSQL instances for each test.
 */
@DataJpaTest
@Import(PostgreSQLTestContainer.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.liquibase.enabled=false"
})
class JpaAccountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JpaAccountRepository repository;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        entityManager.getEntityManager().createQuery("DELETE FROM AccountEntity").executeUpdate();
        entityManager.flush();
        
        // Create test data with different customers and statuses
        LocalDateTime now = LocalDateTime.now();

        // Customer 1234567, Account 001
        // ACTIVE
        AccountEntity testAccount1 = new AccountEntity(
                null,
                "1234567001",  // Customer 1234567, Account 001
                "SAVING",
                new BigDecimal("100.000"),
                1,  // ACTIVE
                now,
                now
        );

        // Customer 1234567, Account 002
        // PENDING
        AccountEntity testAccount2 = new AccountEntity(
                null,
                "1234567002",  // Customer 1234567, Account 002
                "INVESTMENT",
                new BigDecimal("250.500"),
                0,  // PENDING
                now,
                now
        );

        // Customer 7654321, Account 001
        // ACTIVE
        AccountEntity testAccount3 = new AccountEntity(
                null,
                "7654321001",  // Customer 7654321, Account 001
                "SALARY",
                new BigDecimal("500.750"),
                1,  // ACTIVE
                now,
                now
        );

        // Persist test data
        entityManager.persistAndFlush(testAccount1);
        entityManager.persistAndFlush(testAccount2);
        entityManager.persistAndFlush(testAccount3);
    }

    @Test
    void shouldFindAccountsByCustomerNumber() {
        // When - finding accounts for customer 1234567
        List<AccountEntity> accounts = repository.findByCustomerNumber("1234567");
        
        // Then - should return 2 accounts for this customer
        assertEquals(2, accounts.size());
        assertTrue(accounts.stream().anyMatch(a -> a.getAccountNumber().equals("1234567001")));
        assertTrue(accounts.stream().anyMatch(a -> a.getAccountNumber().equals("1234567002")));
    }

    @Test
    void shouldCountAccountsByCustomerNumber() {
        // When - counting accounts for customer 1234567
        long count = repository.countByCustomerNumber("1234567");
        
        // Then - should return 2
        assertEquals(2, count);
        
        // When - counting accounts for customer 7654321
        long singleCount = repository.countByCustomerNumber("7654321");
        
        // Then - should return 1
        assertEquals(1, singleCount);
    }

    @Test
    void shouldFindAccountByAccountNumber() {
        // When - finding an account by specific account number
        Optional<AccountEntity> account = repository.findByAccountNumber("1234567001");
        
        // Then - should find the account
        assertTrue(account.isPresent());
        assertEquals("1234567001", account.get().getAccountNumber());
        assertEquals(new BigDecimal("100.000"), account.get().getBalance());
    }

    @Test
    void shouldNotFindNonExistentAccount() {
        // When - searching for a non-existent account
        Optional<AccountEntity> account = repository.findByAccountNumber("9999999999");
        
        // Then - should return empty
        assertFalse(account.isPresent());
    }

    @Test
    void shouldCheckExistenceByAccountNumber() {
        // When - checking if an account exists
        boolean exists = repository.existsByAccountNumber("1234567001");
        boolean notExists = repository.existsByAccountNumber("9999999999");
        
        // Then - should return correct existence
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void shouldFindActiveAccountsByCustomerNumber() {
        // When - finding active accounts for customer 1234567
        List<AccountEntity> activeAccounts = repository.findByCustomerNumberAndStatus("1234567", 1);
        
        // Then - should return only the active account
        assertEquals(1, activeAccounts.size());
        assertEquals("1234567001", activeAccounts.getFirst().getAccountNumber());
        assertEquals(Integer.valueOf(1), activeAccounts.getFirst().getStatus());
    }

    @Test
    void shouldFindExistingSerialNumbers() {
        // When - finding existing serial numbers for customer 1234567
        List<String> serialNumbers = repository.findExistingSerialNumbers("1234567");
        
        // Then - should return existing serial numbers
        assertEquals(2, serialNumbers.size());
        assertTrue(serialNumbers.contains("001"));
        assertTrue(serialNumbers.contains("002"));
    }
} 