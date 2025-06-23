package com.aalhendi.account_ms.infrastructure.persistence;

import com.aalhendi.account_ms.domain.entities.Account;
import com.aalhendi.account_ms.domain.entities.NewAccount;
import com.aalhendi.account_ms.domain.valueobjects.AccountNumber;
import com.aalhendi.account_ms.domain.valueobjects.AccountStatus;
import com.aalhendi.account_ms.domain.valueobjects.Balance;
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
    void shouldCreateEntityFromConstructor() {
        // Given - entity data
        String accountNumber = "1234567001";
        BigDecimal balance = new BigDecimal("250.750");
        Integer status = 1; // ACTIVE
        LocalDateTime createdAt = testTime;
        LocalDateTime updatedAt = testTime;

        // When - creating an entity from constructor
        AccountEntity entity = new AccountEntity(null, accountNumber, balance, status, createdAt, updatedAt);

        // Then - should have correct properties
        assertEquals(accountNumber, entity.getAccountNumber());
        assertEquals(balance, entity.getBalance());
        assertEquals(status, entity.getStatus());
        assertEquals(createdAt, entity.getCreatedAt());
        assertEquals(updatedAt, entity.getUpdatedAt());
    }

    @Test
    void shouldCreateFromDomainObject() {
        // Given - domain object
        NewAccount newAccount = NewAccount.create(new AccountNumber("1234567001"));
        // We would need to "save" the newAccount to get an Account. We don't have a DB, so we will cheat.
        Account account = Account.reconstitute(1L,
                newAccount.getAccountNumber(),
                newAccount.getBalance(),
                newAccount.getStatus(),
                newAccount.getCreatedAt(),
                newAccount.getUpdatedAt());
        account.activate(); // Activate first
        account.credit(new Balance("500.000")); // Then credit

        // When - creating entity from domain
        AccountEntity entity = AccountEntity.fromDomain(account);

        // Then - should match domain properties
        assertEquals("1234567001", entity.getAccountNumber());
        assertEquals(new BigDecimal("500.000"), entity.getBalance());
        assertEquals(AccountStatus.ACTIVE.getCode(), entity.getStatus());
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    void shouldUpdateTimestampWithTouch() {
        // Given - entity with old timestamp
        LocalDateTime oldTime = testTime.minusHours(1);
        accountEntity.setCreatedAt(testTime); // Set createdAt first
        accountEntity.setUpdatedAt(oldTime);

        // When - touching entity
        accountEntity.touch();

        // Then - updatedAt should be more recent
        assertTrue(accountEntity.getUpdatedAt().isAfter(oldTime));
        assertNotNull(accountEntity.getCreatedAt());
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
    void shouldReturnNullForInvalidAccountNumber() {
        // When - setting invalid account number
        // TODO(aalhendi): do i want to override setters/getters to fail on null?
        accountEntity.setAccountNumber(null);

        // Then - customer number should be null
        assertNull(accountEntity.getCustomerNumber());

        // When - setting short account number
        accountEntity.setAccountNumber("123");

        // Then - customer number should be null
        assertNull(accountEntity.getCustomerNumber());
    }

    @Test
    void shouldDetectActiveStatus() {
        // Given - active account
        accountEntity.setStatus(1);

        // When - checking if active
        boolean isActive = accountEntity.isActive();

        // Then - should be active
        assertTrue(isActive);
    }

    @Test
    void shouldHandleEqualsAndHashCode() {
        // Given - two entities with the same account number
        AccountEntity entity1 = new AccountEntity();
        entity1.setAccountNumber("1234567001");

        AccountEntity entity2 = new AccountEntity();
        entity2.setAccountNumber("1234567001");

        AccountEntity entity3 = new AccountEntity();
        entity3.setAccountNumber("1234567002");

        // Then - entities with the same account number should be equal
        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());

        // And - entities with different account numbers should not be equal
        assertNotEquals(entity1, entity3);
        assertNotEquals(entity1.hashCode(), entity3.hashCode());
    }

    @Test
    void shouldHandleNullInEqualsAndHashCode() {
        // Given - entities with null account numbers
        AccountEntity entity1 = new AccountEntity();
        entity1.setAccountNumber(null);

        AccountEntity entity2 = new AccountEntity();
        entity2.setAccountNumber(null);

        AccountEntity entity3 = new AccountEntity();
        entity3.setAccountNumber("1234567001");

        // Then - entities with null account numbers should be equal
        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());

        // And - null should not equal non-null
        assertNotEquals(entity1, entity3);
    }

    @Test
    void shouldHaveToStringRepresentation() {
        // Given - populated entity
        accountEntity.setAccountNumber("1234567001");
        accountEntity.setBalance(new BigDecimal("100.000"));
        accountEntity.setStatus(1);

        // When - calling toString
        String toString = accountEntity.toString();

        // Then - should contain key information
        assertNotNull(toString);
        assertTrue(toString.contains("AccountEntity"));
        assertTrue(toString.contains("1234567001"));
        assertTrue(toString.contains("100.000"));
    }

    @Test
    void shouldCreateEntityWithAllFields() {
        // Given - constructor parameters
        String accountNumber = "1234567001";
        BigDecimal balance = new BigDecimal("500.000");
        Integer status = 1;
        LocalDateTime createdAt = testTime;
        LocalDateTime updatedAt = testTime;

        // When - creating entity with constructor
        AccountEntity entity = new AccountEntity(null, accountNumber, balance, status, createdAt, updatedAt);

        // Then - properties should be set correctly
        assertEquals(accountNumber, entity.getAccountNumber());
        assertEquals(balance, entity.getBalance());
        assertEquals(status, entity.getStatus());
        assertEquals(createdAt, entity.getCreatedAt());
        assertEquals(updatedAt, entity.getUpdatedAt());
    }
} 