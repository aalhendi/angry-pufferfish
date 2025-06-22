package com.aalhendi.account_ms.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AccountEntity JPA mapping.
 */
class AccountEntityTest {

    private AccountEntity accountEntity;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        testTime = LocalDateTime.now();
        accountEntity = new AccountEntity();
    }

    @Test
    void shouldCreateAccountEntityWithBasicProperties() {
        // Given - basic account data
        String expectedAccountNumber = "1234567001";
        BigDecimal expectedBalance = new BigDecimal("100.500");
        Integer expectedStatus = 1;

        // When - setting properties
        accountEntity.setAccountNumber(expectedAccountNumber);
        accountEntity.setBalance(expectedBalance);
        accountEntity.setStatus(expectedStatus);
        accountEntity.setCreatedAt(testTime);
        accountEntity.setUpdatedAt(testTime);

        // Then - properties should be retrievable
        assertEquals(expectedAccountNumber, accountEntity.getAccountNumber());
        assertEquals(expectedBalance, accountEntity.getBalance());
        assertEquals(expectedStatus, accountEntity.getStatus());
        assertEquals(testTime, accountEntity.getCreatedAt());
        assertEquals(testTime, accountEntity.getUpdatedAt());
    }

    @Test
    void shouldHandleNullableDeletedAt() {
        // Given - entity without deleted timestamp
        accountEntity.setDeletedAt(null);

        // Then - should handle null gracefully
        assertNull(accountEntity.getDeletedAt());

        // When - setting deleted timestamp
        accountEntity.setDeletedAt(testTime);

        // Then - should store the timestamp
        assertEquals(testTime, accountEntity.getDeletedAt());
    }

    @Test
    void shouldConvertFromDomainAccount() {
        // Given - a domain account
        AccountNumber accountNumber = AccountNumber.of("1234567001");
        Balance balance = Balance.of("250.750");
        AccountStatus status = AccountStatus.ACTIVE;
        
        Account domainAccount = Account.reconstitute(
            1L, accountNumber, balance, status, testTime, testTime, null
        );

        // When - converting from domain
        AccountEntity entity = AccountEntity.fromDomain(domainAccount);

        // Then - entity should have correct values
        assertEquals(1L, entity.getId());
        assertEquals("1234567001", entity.getAccountNumber());
        assertEquals(new BigDecimal("250.750"), entity.getBalance());
        assertEquals(AccountStatus.ACTIVE.getCode(), entity.getStatus());
        assertEquals(testTime, entity.getCreatedAt());
        assertEquals(testTime, entity.getUpdatedAt());
        assertNull(entity.getDeletedAt());
    }

    @Test
    void shouldConvertToDomainAccount() {
        // Given - an entity with data
        accountEntity.setId(2L);
        accountEntity.setAccountNumber("7654321005");
        accountEntity.setBalance(new BigDecimal("1000.000"));
        accountEntity.setStatus(AccountStatus.PENDING.getCode());
        accountEntity.setCreatedAt(testTime);
        accountEntity.setUpdatedAt(testTime);
        accountEntity.setDeletedAt(null);

        // When - converting to domain
        Account domainAccount = accountEntity.toDomain();

        // Then - domain account should have correct values
        assertEquals(2L, domainAccount.getId());
        assertEquals("7654321005", domainAccount.getAccountNumber().getValue());
        assertEquals(new BigDecimal("1000.000"), domainAccount.getBalance().getAmount());
        assertEquals(AccountStatus.PENDING, domainAccount.getStatus());
        assertEquals(testTime, domainAccount.getCreatedAt());
        assertEquals(testTime, domainAccount.getUpdatedAt());
    }

    @Test
    void shouldUpdateFromDomainAccount() {
        // Given - existing entity and updated domain account
        accountEntity.setId(3L);
        accountEntity.setAccountNumber("1111111003");
        accountEntity.setBalance(new BigDecimal("500.000"));
        accountEntity.setStatus(AccountStatus.PENDING.getCode());
        accountEntity.setCreatedAt(testTime);
        accountEntity.setUpdatedAt(testTime);

        LocalDateTime laterTime = testTime.plusMinutes(10);
        Account updatedDomainAccount = Account.reconstitute(
            3L, 
            AccountNumber.of("1111111003"),
            Balance.of("750.250"),
            AccountStatus.ACTIVE,
            testTime,
            laterTime,
            null
        );

        // When - updating from domain
        accountEntity.updateFromDomain(updatedDomainAccount);

        // Then - mutable fields should be updated
        assertEquals(new BigDecimal("750.250"), accountEntity.getBalance());
        assertEquals(AccountStatus.ACTIVE.getCode(), accountEntity.getStatus());
        assertEquals(laterTime, accountEntity.getUpdatedAt());
        // Immutable fields should remain unchanged
        assertEquals(3L, accountEntity.getId());
        assertEquals("1111111003", accountEntity.getAccountNumber());
        assertEquals(testTime, accountEntity.getCreatedAt());
    }

    @Test
    void shouldExtractCustomerNumberFromAccountNumber() {
        // Given - account number with customer prefix
        accountEntity.setAccountNumber("9876543002");

        // When - extracting customer number
        String customerNumber = accountEntity.getCustomerNumber();

        // Then - should return first 7 digits
        assertEquals("9876543", customerNumber);
    }

    @Test
    void shouldHandleNullAccountNumberInCustomerExtraction() {
        // Given - null account number
        accountEntity.setAccountNumber(null);

        // When - extracting customer number
        String customerNumber = accountEntity.getCustomerNumber();

        // Then - should return null
        assertNull(customerNumber);
    }

    @Test
    void shouldHandleShortAccountNumberInCustomerExtraction() {
        // Given - account number shorter than 7 characters
        accountEntity.setAccountNumber("12345");

        // When - extracting customer number
        String customerNumber = accountEntity.getCustomerNumber();

        // Then - should return null (invalid format)
        assertNull(customerNumber);
    }

    @Test
    void shouldImplementEqualsBasedOnAccountNumber() {
        // Given - two entities with same account number
        AccountEntity entity1 = new AccountEntity();
        entity1.setAccountNumber("1234567001");
        
        AccountEntity entity2 = new AccountEntity();
        entity2.setAccountNumber("1234567001");

        AccountEntity entity3 = new AccountEntity();
        entity3.setAccountNumber("1234567002");

        // Then - equals should be based on account number
        assertEquals(entity1, entity2);
        assertNotEquals(entity1, entity3);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void shouldHandleEqualsWithNullAccountNumber() {
        // Given - entities with null account numbers
        AccountEntity entity1 = new AccountEntity();
        entity1.setAccountNumber(null);
        
        AccountEntity entity2 = new AccountEntity();
        entity2.setAccountNumber(null);

        AccountEntity entity3 = new AccountEntity();
        entity3.setAccountNumber("1234567001");

        // Then - should handle null comparisons properly
        assertEquals(entity1, entity2);
        assertNotEquals(entity1, entity3);
        assertNotEquals(entity3, entity1);
    }

    @Test
    void shouldProvideToStringRepresentation() {
        // Given - entity with data
        accountEntity.setAccountNumber("1234567001");
        accountEntity.setBalance(new BigDecimal("100.500"));
        accountEntity.setStatus(1); // ACTIVE status code

        // When - calling toString
        String stringRepresentation = accountEntity.toString();

        // Then - should contain key information
        assertNotNull(stringRepresentation);
        assertTrue(stringRepresentation.contains("1234567001"));
        assertTrue(stringRepresentation.contains("100.500"));
        assertTrue(stringRepresentation.contains("AccountEntity"));
    }

    @Test
    void shouldCreateEntityWithParameterizedConstructor() {
        // Given - constructor parameters
        String accountNumber = "1234567001";
        BigDecimal balance = new BigDecimal("500.000");
        Integer status = 1;
        LocalDateTime createdAt = testTime;
        LocalDateTime updatedAt = testTime;

        // When - creating entity with constructor
        AccountEntity entity = new AccountEntity(accountNumber, balance, status, createdAt, updatedAt, null);

        // Then - properties should be set correctly
        assertEquals(accountNumber, entity.getAccountNumber());
        assertEquals(balance, entity.getBalance());
        assertEquals(status, entity.getStatus());
        assertEquals(createdAt, entity.getCreatedAt());
        assertEquals(updatedAt, entity.getUpdatedAt());
        assertNull(entity.getDeletedAt());
    }
} 