package com.aalhendi.account_ms.domain.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AccountNumber value object.
 * Tests validation, formatting, and business rules for account numbers.
 */
@DisplayName("AccountNumber Value Object")
class AccountNumberTest {

    @Test
    @DisplayName("Should create valid account number")
    void shouldCreateValidAccountNumber() {
        // Given - valid 10-digit account number
        String validAccountNumber = "1234567001";

        // When - creating AccountNumber
        AccountNumber accountNumber = new AccountNumber(validAccountNumber);

        // Then - should create successfully
        assertNotNull(accountNumber);
        assertEquals(validAccountNumber, accountNumber.value());
        assertEquals("AccountNumber[value=" + validAccountNumber + "]", accountNumber.toString());
    }

    @Test
    @DisplayName("Should extract customer number correctly")
    void shouldExtractCustomerNumber() {
        // Given - account number with known customer number
        AccountNumber accountNumber = new AccountNumber("1234567001");

        // When - extracting customer number
        String customerNumber = accountNumber.customerNumber();

        // Then - should return the first 7 digits
        assertEquals("1234567", customerNumber);
    }

    @Test
    @DisplayName("Should extract serial number correctly")
    void shouldExtractSerialNumber() {
        // Given - account number with known serial
        AccountNumber accountNumber = new AccountNumber("1234567001");

        // When - extracting serial number
        String serialNumber = accountNumber.serialNumber();

        // Then - should return the last 3 digits
        assertEquals("001", serialNumber);
    }

    @Test
    @DisplayName("Should reject null account number")
    void shouldRejectNullAccountNumber() {
        // When & Then - creating with null should throw
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AccountNumber(null)
        );
        assertEquals("Account number cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject account number with wrong length")
    void shouldRejectWrongLength() {
        // Given - account numbers with wrong lengths
        String tooShort = "123456789";   // 9 digits
        String tooLong = "12345678901";  // 11 digits

        // When & Then - should throw for too short
        IllegalArgumentException shortException = assertThrows(
                IllegalArgumentException.class,
                () -> new AccountNumber(tooShort)
        );
        assertEquals("Account number must be exactly 10 digits", shortException.getMessage());

        // When & Then - should throw for too long
        IllegalArgumentException longException = assertThrows(
                IllegalArgumentException.class,
                () -> new AccountNumber(tooLong)
        );
        assertEquals("Account number must be exactly 10 digits", longException.getMessage());
    }

    @Test
    @DisplayName("Should reject non-numeric account number")
    void shouldRejectNonNumeric() {
        // Given - non-numeric account numbers
        String withLetters = "123456789a";      // 10 chars but has letter
        String withSpecialChars = "123456789!";  // 10 chars but has special char
        String withSpaces = "123 456 78";       // 10 chars but has spaces

        // When & Then - should throw for letters
        IllegalArgumentException lettersException = assertThrows(
                IllegalArgumentException.class,
                () -> new AccountNumber(withLetters)
        );
        assertEquals("Account number must contain only digits", lettersException.getMessage());

        // When & Then - should throw for special characters
        IllegalArgumentException specialException = assertThrows(
                IllegalArgumentException.class,
                () -> new AccountNumber(withSpecialChars)
        );
        assertEquals("Account number must contain only digits", specialException.getMessage());

        // When & Then - should throw for spaces (this will be 11 chars, so length error first)
        IllegalArgumentException spaceException = assertThrows(
                IllegalArgumentException.class,
                () -> new AccountNumber("123 456 789")  // 11 chars total
        );
        assertEquals("Account number must be exactly 10 digits", spaceException.getMessage());

        // When & Then - should throw for spaces in a 10-char string
        IllegalArgumentException spaceFormatException = assertThrows(
                IllegalArgumentException.class,
                () -> new AccountNumber(withSpaces)  // exactly 10 chars but has spaces
        );
        assertEquals("Account number must contain only digits", spaceFormatException.getMessage());
    }

    @Test
    @DisplayName("Should handle equals and hashCode correctly")
    void shouldHandleEqualsAndHashCode() {
        // Given - same account numbers
        AccountNumber accountNumber1 = new AccountNumber("1234567001");
        AccountNumber accountNumber2 = new AccountNumber("1234567001");
        AccountNumber accountNumber3 = new AccountNumber("1234567002");

        // Then - equal account numbers should be equal
        assertEquals(accountNumber1, accountNumber2);
        assertEquals(accountNumber1.hashCode(), accountNumber2.hashCode());

        // And - different account numbers should not be equal
        assertNotEquals(accountNumber1, accountNumber3);
        assertNotEquals(accountNumber1.hashCode(), accountNumber3.hashCode());

        // And - should not equal null or different types
        assertNotEquals(null, accountNumber1);
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals("1234567001", accountNumber1);
    }

    @Test
    @DisplayName("Should handle various customer and serial combinations")
    void shouldHandleVariousCombinations() {
        // Given - different account number patterns
        AccountNumber minAccount = new AccountNumber("0000001001");  // Min customer, first account
        AccountNumber maxAccount = new AccountNumber("9999999010");  // Max customer, last account

        // Then - should extract correctly
        assertEquals("0000001", minAccount.customerNumber());
        assertEquals("001", minAccount.serialNumber());

        assertEquals("9999999", maxAccount.customerNumber());
        assertEquals("010", maxAccount.serialNumber());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    @DisplayName("Should be immutable")
    void shouldBeImmutable() {
        // Given - account number
        AccountNumber accountNumber = new AccountNumber("1234567001");
        String originalValue = accountNumber.value();

        // When - calling methods
        accountNumber.customerNumber();
        accountNumber.serialNumber();
        accountNumber.toString();

        // Then - value should remain unchanged
        assertEquals(originalValue, accountNumber.value());
    }
} 