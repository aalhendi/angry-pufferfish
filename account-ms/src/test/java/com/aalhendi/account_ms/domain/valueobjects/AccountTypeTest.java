package com.aalhendi.account_ms.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AccountType enum.
 */
@DisplayName("AccountType")
class AccountTypeTest {

    @Test
    @DisplayName("Should have all required account types")
    void shouldHaveAllRequiredAccountTypes() {
        // When - getting all account types
        AccountType[] types = AccountType.values();

        // Then - should have exactly 3 types
        assertEquals(3, types.length);

        // And - should contain all expected types
        assertTrue(containsType(types, AccountType.SAVING));
        assertTrue(containsType(types, AccountType.INVESTMENT));
        assertTrue(containsType(types, AccountType.SALARY));
    }

    @ParameterizedTest
    @EnumSource(AccountType.class)
    @DisplayName("Should have correct string values")
    void shouldHaveCorrectStringValues(AccountType accountType) {
        // When - getting string value
        String value = accountType.name();

        // Then - should match enum name
        assertEquals(accountType.name(), value);
        assertEquals(accountType.name(), accountType.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"SAVING", "INVESTMENT", "SALARY"})
    @DisplayName("Should create from valid string values")
    void shouldCreateFromValidStringValues(String validType) {
        // When - creating from string
        AccountType accountType = AccountType.fromString(validType);

        // Then - should create successfully
        assertNotNull(accountType);
        assertEquals(validType, accountType.name());
    }

    @ParameterizedTest
    @ValueSource(strings = {"saving", "investment", "salary", "SaViNg", "INVESTMENT", "salary"})
    @DisplayName("Should handle case insensitive input")
    void shouldHandleCaseInsensitiveInput(String mixedCaseType) {
        // When - creating from mixed case string
        AccountType accountType = AccountType.fromString(mixedCaseType);

        // Then - should create successfully
        assertNotNull(accountType);
        assertEquals(mixedCaseType.toUpperCase(), accountType.name());
    }

    @ParameterizedTest
    @ValueSource(strings = {"CURRENT", "INVALID", "CHECKING", "", "  ", "NULL"})
    @DisplayName("Should reject invalid account types")
    void shouldRejectInvalidAccountTypes(String invalidType) {
        // When & Then - should throw exception
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> AccountType.fromString(invalidType)
        );

        // And - should have meaningful error message
        assertTrue(exception.getMessage().contains("Invalid account type"));
        assertTrue(exception.getMessage().contains(invalidType));
        assertTrue(exception.getMessage().contains("SAVING, INVESTMENT, SALARY"));
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("Should reject null account type")
    void shouldRejectNullAccountType(String nullType) {
        // When & Then - should throw exception
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> AccountType.fromString(nullType)
        );

        // And - should have a meaningful error message
        assertTrue(exception.getMessage().contains("Account type cannot be null"));
    }

    @Test
    @DisplayName("Should correctly identify salary accounts")
    void shouldCorrectlyIdentifySalaryAccounts() {
        // Given - different account types
        AccountType salary = AccountType.SALARY;
        AccountType saving = AccountType.SAVING;
        AccountType investment = AccountType.INVESTMENT;

        // Then - only salary should return true for isSalary()
        assertTrue(salary.isSalary());
        assertFalse(saving.isSalary());
        assertFalse(investment.isSalary());
    }

    @Test
    @DisplayName("Should handle enum equality correctly")
    void shouldHandleEnumEqualityCorrectly() {
        // Given - same account types from different sources
        AccountType saving1 = AccountType.SAVING;
        AccountType saving2 = AccountType.fromString("SAVING");
        AccountType investment = AccountType.INVESTMENT;

        // Then - same types should be equal
        assertEquals(saving1, saving2);
        assertEquals(saving1.hashCode(), saving2.hashCode());

        // And - different types should not be equal
        assertNotEquals(saving1, investment);
        assertNotEquals(saving1.hashCode(), investment.hashCode());
    }

    @Test
    @DisplayName("Should be comparable and ordinal")
    void shouldBeComparableAndOrdinal() {
        // Given - account types
        AccountType saving = AccountType.SAVING;
        AccountType investment = AccountType.INVESTMENT;
        AccountType salary = AccountType.SALARY;

        // Then - should have predictable ordinals
        assertEquals(0, saving.ordinal());
        assertEquals(1, investment.ordinal());
        assertEquals(2, salary.ordinal());

        // And - should be comparable
        assertTrue(saving.compareTo(investment) < 0);
        assertTrue(investment.compareTo(salary) < 0);
        assertTrue(salary.compareTo(saving) > 0);
    }

    @Test
    @DisplayName("Should support business logic scenarios")
    void shouldSupportBusinessLogicScenarios() {
        // Scenario 1: Customer can only have one salary account
        AccountType salaryType = AccountType.fromString("SALARY");
        assertTrue(salaryType.isSalary(), "Salary accounts should be identifiable for business rules");
    }

    @Test
    @DisplayName("Should have consistent string representation")
    void shouldHaveConsistentStringRepresentation() {
        // Given - all account types
        for (AccountType type : AccountType.values()) {
            // When - converting to string in different ways
            String directString = type.toString();
            String nameString = type.name();

            // Then - all should be consistent
            assertEquals(nameString, directString);

            // And - round-trip should work
            AccountType roundTrip = AccountType.fromString(directString);
            assertEquals(type, roundTrip);
        }
    }

    /**
     * Helper method to check if array contains a specific account type.
     */
    private boolean containsType(AccountType[] types, AccountType target) {
        for (AccountType type : types) {
            if (type == target) {
                return true;
            }
        }
        return false;
    }
} 