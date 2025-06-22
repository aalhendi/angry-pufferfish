package com.aalhendi.account_ms.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for JpaAccountRepository.
 */
@DataJpaTest
@ActiveProfiles("test")
class JpaAccountRepositoryTest {

    // Repository will be injected through TDD
    private JpaAccountRepository jpaAccountRepository;

    @Test
    void shouldFindAccountByAccountNumber() {
        // Given - saved account entity
        AccountEntity account = createTestAccount("1234567001", new BigDecimal("100.00"));
        jpaAccountRepository.save(account);

        // When - finding by account number
        Optional<AccountEntity> found = jpaAccountRepository.findByAccountNumber("1234567001");

        // Then - should find the account
        assertTrue(found.isPresent());
        assertEquals("1234567001", found.get().getAccountNumber());
    }

    @Test
    void shouldNotFindNonExistentAccount() {
        // When - searching for non-existent account
        Optional<AccountEntity> found = jpaAccountRepository.findByAccountNumber("9999999999");

        // Then - should not find anything
        assertFalse(found.isPresent());
    }

    @Test
    void shouldFindAccountsByCustomerNumber() {
        // Given - multiple accounts for same customer
        AccountEntity account1 = createTestAccount("1234567001", new BigDecimal("100.00"));
        AccountEntity account2 = createTestAccount("1234567002", new BigDecimal("200.00"));
        AccountEntity account3 = createTestAccount("7654321001", new BigDecimal("300.00"));
        
        jpaAccountRepository.save(account1);
        jpaAccountRepository.save(account2);
        jpaAccountRepository.save(account3);

        // When - finding by customer number
        List<AccountEntity> accounts = jpaAccountRepository.findByCustomerNumber("1234567");

        // Then - should find customer's accounts only
        assertEquals(2, accounts.size());
        assertTrue(accounts.stream().allMatch(acc -> acc.getAccountNumber().startsWith("1234567")));
    }

    @Test
    void shouldFindActiveAccountsByCustomerNumber() {
        // Given - mix of active and inactive accounts
        AccountEntity activeAccount = createTestAccount("1234567001", new BigDecimal("100.00"));
        activeAccount.setStatus(1); // ACTIVE
        
        AccountEntity inactiveAccount = createTestAccount("1234567002", new BigDecimal("200.00"));
        inactiveAccount.setStatus(0); // INACTIVE
        
        jpaAccountRepository.save(activeAccount);
        jpaAccountRepository.save(inactiveAccount);

        // When - finding active accounts only
        List<AccountEntity> activeAccounts = jpaAccountRepository.findActiveAccountsByCustomerNumber("1234567");

        // Then - should find only active accounts
        assertEquals(1, activeAccounts.size());
        assertEquals(1, activeAccounts.get(0).getStatus());
    }

    @Test
    void shouldCountAccountsByCustomerNumber() {
        // Given - multiple accounts for customer
        jpaAccountRepository.save(createTestAccount("1234567001", new BigDecimal("100.00")));
        jpaAccountRepository.save(createTestAccount("1234567002", new BigDecimal("200.00")));
        jpaAccountRepository.save(createTestAccount("7654321001", new BigDecimal("300.00")));

        // When - counting customer accounts
        long count = jpaAccountRepository.countByCustomerNumber("1234567");

        // Then - should return correct count
        assertEquals(2, count);
    }

    @Test
    void shouldCheckExistenceByAccountNumber() {
        // Given - saved account
        jpaAccountRepository.save(createTestAccount("1234567001", new BigDecimal("100.00")));

        // When - checking existence
        boolean exists = jpaAccountRepository.existsByAccountNumber("1234567001");
        boolean notExists = jpaAccountRepository.existsByAccountNumber("9999999999");

        // Then - should return correct existence status
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void shouldFindExistingSerialNumbers() {
        // Given - accounts with serial numbers
        jpaAccountRepository.save(createTestAccount("1234567001", new BigDecimal("100.00")));
        jpaAccountRepository.save(createTestAccount("1234567003", new BigDecimal("200.00")));
        jpaAccountRepository.save(createTestAccount("1234567005", new BigDecimal("300.00")));

        // When - finding existing serials
        List<String> serials = jpaAccountRepository.findExistingSerialNumbers("1234567");

        // Then - should return serial numbers only
        assertEquals(3, serials.size());
        assertTrue(serials.contains("001"));
        assertTrue(serials.contains("003"));
        assertTrue(serials.contains("005"));
    }

    private AccountEntity createTestAccount(String accountNumber, BigDecimal balance) {
        AccountEntity account = new AccountEntity();
        account.setAccountNumber(accountNumber);
        account.setBalance(balance);
        account.setStatus(1); // ACTIVE
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        return account;
    }
} 