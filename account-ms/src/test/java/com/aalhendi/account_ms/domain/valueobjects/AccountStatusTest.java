package com.aalhendi.account_ms.domain.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AccountStatus enum.
 */
@DisplayName("AccountStatus Enum")
class AccountStatusTest {

    @Test
    @DisplayName("Should have correct status codes")
    void shouldHaveCorrectStatusCodes() {
        // Then - status codes should match expected values
        assertEquals(0, AccountStatus.PENDING.getCode());
        assertEquals(1, AccountStatus.ACTIVE.getCode());
        assertEquals(2, AccountStatus.SUSPENDED.getCode());
        assertEquals(3, AccountStatus.FROZEN.getCode());
        assertEquals(4, AccountStatus.CLOSED.getCode());
    }

    @Test
    @DisplayName("Should create status from code")
    void shouldCreateStatusFromCode() {
        // When - creating from codes
        AccountStatus pending = AccountStatus.fromCode(0);
        AccountStatus active = AccountStatus.fromCode(1);
        AccountStatus suspended = AccountStatus.fromCode(2);
        AccountStatus frozen = AccountStatus.fromCode(3);
        AccountStatus closed = AccountStatus.fromCode(4);
        
        // Then - should return correct statuses
        assertEquals(AccountStatus.PENDING, pending);
        assertEquals(AccountStatus.ACTIVE, active);
        assertEquals(AccountStatus.SUSPENDED, suspended);
        assertEquals(AccountStatus.FROZEN, frozen);
        assertEquals(AccountStatus.CLOSED, closed);
    }

    @Test
    @DisplayName("Should reject invalid status codes")
    void shouldRejectInvalidStatusCodes() {
        // Given - invalid status codes
        int[] invalidCodes = {-1, 5, 10, 100, Integer.MAX_VALUE, Integer.MIN_VALUE};
        
        // When & Then - should throw for each invalid code
        for (int invalidCode : invalidCodes) {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> AccountStatus.fromCode(invalidCode),
                "Should reject invalid code: " + invalidCode
            );
            assertEquals("Invalid account status code: " + invalidCode, exception.getMessage());
        }
    }

    @Test
    @DisplayName("Should correctly identify which statuses allow transactions")
    void shouldIdentifyTransactionAllowedStatuses() {
        // Then - only ACTIVE should allow transactions
        assertFalse(AccountStatus.PENDING.allowsTransactions());
        assertTrue(AccountStatus.ACTIVE.allowsTransactions());
        assertFalse(AccountStatus.SUSPENDED.allowsTransactions());
        assertFalse(AccountStatus.FROZEN.allowsTransactions());
        assertFalse(AccountStatus.CLOSED.allowsTransactions());
    }

    @Test
    @DisplayName("Should correctly identify active status")
    void shouldIdentifyActiveStatus() {
        // Then - only ACTIVE should be considered active
        assertFalse(AccountStatus.PENDING.isActive());
        assertTrue(AccountStatus.ACTIVE.isActive());
        assertFalse(AccountStatus.SUSPENDED.isActive());
        assertFalse(AccountStatus.FROZEN.isActive());
        assertFalse(AccountStatus.CLOSED.isActive());
    }

    @Test
    @DisplayName("Should correctly identify closed status")
    void shouldIdentifyClosedStatus() {
        // Then - only CLOSED should be considered closed
        assertFalse(AccountStatus.PENDING.isClosed());
        assertFalse(AccountStatus.ACTIVE.isClosed());
        assertFalse(AccountStatus.SUSPENDED.isClosed());
        assertFalse(AccountStatus.FROZEN.isClosed());
        assertTrue(AccountStatus.CLOSED.isClosed());
    }

    @Test
    @DisplayName("Should have all expected status values")
    void shouldHaveAllExpectedStatusValues() {
        // Given - expected status count
        AccountStatus[] allStatuses = AccountStatus.values();
        
        // Then - should have exactly 5 statuses
        assertEquals(5, allStatuses.length);
        
        // And - should contain all expected statuses
        assertTrue(contains(allStatuses, AccountStatus.PENDING));
        assertTrue(contains(allStatuses, AccountStatus.ACTIVE));
        assertTrue(contains(allStatuses, AccountStatus.SUSPENDED));
        assertTrue(contains(allStatuses, AccountStatus.FROZEN));
        assertTrue(contains(allStatuses, AccountStatus.CLOSED));
    }

    @Test
    @DisplayName("Should maintain consistent code-to-status mapping")
    void shouldMaintainConsistentCodeToStatusMapping() {
        // Given - all status values
        AccountStatus[] allStatuses = AccountStatus.values();
        
        // Then - fromCode should be inverse of getCode for all statuses
        for (AccountStatus status : allStatuses) {
            AccountStatus recovered = AccountStatus.fromCode(status.getCode());
            assertEquals(status, recovered, 
                "Code " + status.getCode() + " should map back to " + status);
        }
    }

    @Test
    @DisplayName("Should have unique codes for all statuses")
    void shouldHaveUniqueCodesForAllStatuses() {
        // Given - all status values
        AccountStatus[] allStatuses = AccountStatus.values();
        
        // Then - all codes should be unique
        for (int i = 0; i < allStatuses.length; i++) {
            for (int j = i + 1; j < allStatuses.length; j++) {
                assertNotEquals(allStatuses[i].getCode(), allStatuses[j].getCode(),
                    allStatuses[i] + " and " + allStatuses[j] + " should have different codes");
            }
        }
    }

    @Test
    @DisplayName("Should have meaningful string representation")
    void shouldHaveMeaningfulStringRepresentation() {
        // Then - toString should return the enum name
        assertEquals("PENDING", AccountStatus.PENDING.toString());
        assertEquals("ACTIVE", AccountStatus.ACTIVE.toString());
        assertEquals("SUSPENDED", AccountStatus.SUSPENDED.toString());
        assertEquals("FROZEN", AccountStatus.FROZEN.toString());
        assertEquals("CLOSED", AccountStatus.CLOSED.toString());
    }

    @Test
    @DisplayName("Should support valueOf operations")
    void shouldSupportValueOfOperations() {
        // When - using valueOf
        AccountStatus pending = AccountStatus.valueOf("PENDING");
        AccountStatus active = AccountStatus.valueOf("ACTIVE");
        AccountStatus suspended = AccountStatus.valueOf("SUSPENDED");
        AccountStatus frozen = AccountStatus.valueOf("FROZEN");
        AccountStatus closed = AccountStatus.valueOf("CLOSED");
        
        // Then - should return correct instances
        assertEquals(AccountStatus.PENDING, pending);
        assertEquals(AccountStatus.ACTIVE, active);
        assertEquals(AccountStatus.SUSPENDED, suspended);
        assertEquals(AccountStatus.FROZEN, frozen);
        assertEquals(AccountStatus.CLOSED, closed);
    }

    @Test
    @DisplayName("Should reject invalid valueOf strings")
    void shouldRejectInvalidValueOfStrings() {
        // Given - invalid status names
        String[] invalidNames = {"INVALID", "active", "Pending", "", null};
        
        // When & Then - should throw for invalid names
        for (String invalidName : invalidNames) {
            if (invalidName == null) {
                assertThrows(NullPointerException.class, () -> AccountStatus.valueOf(invalidName));
            } else {
                assertThrows(IllegalArgumentException.class, () -> AccountStatus.valueOf(invalidName));
            }
        }
    }

    @Test
    @DisplayName("Should handle business logic transitions correctly")
    void shouldHandleBusinessLogicTransitions() {
        // Given - understanding of typical account lifecycle
        /*
         PENDING -> ACTIVE (account activation)
         ACTIVE -> SUSPENDED (temporary suspension)
         ACTIVE -> FROZEN
         SUSPENDED -> ACTIVE (reactivation)
         FROZEN -> ACTIVE (unfreeze)
         Any status -> CLOSED (account closure)
        */
        
        // Then - business rules should be enforced by the properties
        
        // Only ACTIVE accounts can perform transactions
        assertTrue(AccountStatus.ACTIVE.allowsTransactions());
        assertFalse(AccountStatus.PENDING.allowsTransactions());
        assertFalse(AccountStatus.SUSPENDED.allowsTransactions());
        assertFalse(AccountStatus.FROZEN.allowsTransactions());
        assertFalse(AccountStatus.CLOSED.allowsTransactions());
        
        // Only CLOSED is considered permanently closed
        assertTrue(AccountStatus.CLOSED.isClosed());
        assertFalse(AccountStatus.PENDING.isClosed());
        assertFalse(AccountStatus.ACTIVE.isClosed());
        assertFalse(AccountStatus.SUSPENDED.isClosed());
        assertFalse(AccountStatus.FROZEN.isClosed());
    }

    // Helper method to check if array contains a value
    private boolean contains(AccountStatus[] array, AccountStatus value) {
        for (AccountStatus status : array) {
            if (status == value) {
                return true;
            }
        }
        return false;
    }
} 