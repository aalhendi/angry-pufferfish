package com.aalhendi.account_ms.domain.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for a Balance value object.
 */
@DisplayName("Balance Value Object")
class BalanceTest {

    @Test
    @DisplayName("Should create balance from BigDecimal")
    void shouldCreateBalanceFromBigDecimal() {
        // Given - valid BigDecimal amount
        BigDecimal amount = new BigDecimal("100.500");
        
        // When - creating Balance
        Balance balance = new Balance(amount);
        
        // Then - should create successfully with proper precision
        assertNotNull(balance);
        assertEquals(new BigDecimal("100.500"), balance.value());
        assertEquals("Balance[value=100.500]", balance.toString());
    }

    @Test
    @DisplayName("Should create balance from string")
    void shouldCreateBalanceFromString() {
        // Given - valid string amount
        String amount = "250.750";
        
        // When - creating Balance
        Balance balance = new Balance(amount);
        
        // Then - should create successfully
        assertNotNull(balance);
        assertEquals(new BigDecimal("250.750"), balance.value());
        assertEquals("Balance[value=250.750]", balance.toString());
    }

    @Test
    @DisplayName("Should create zero balance")
    void shouldCreateZeroBalance() {
        // When - creating zero balance
        Balance balance = Balance.ZERO;
        
        // Then - should be zero with proper precision
        assertNotNull(balance);
        assertEquals(new BigDecimal("0.000"), balance.value());
        assertTrue(balance.isEqualTo(new Balance("0.000")));
    }

    @Test
    @DisplayName("Should enforce 3 decimal places precision")
    void shouldEnforceThreeDecimalPrecision() {
        // Given - amounts with different precision
        Balance fromTwoDecimals = new Balance("100.50");
        Balance fromFourDecimals = new Balance("100.5000");
        Balance fromInteger = new Balance("100");
        
        // Then - should all have 3 decimal places
        assertEquals(new BigDecimal("100.500"), fromTwoDecimals.value());
        assertEquals(new BigDecimal("100.500"), fromFourDecimals.value());
        assertEquals(new BigDecimal("100.000"), fromInteger.value());
    }

    @Test
    @DisplayName("Should add balances correctly")
    void shouldAddBalances() {
        // Given - two balances
        Balance balance1 = new Balance("100.500");
        Balance balance2 = new Balance("50.250");
        
        // When - adding them
        Balance result = balance1.add(balance2);
        
        // Then - should return a correct sum
        assertEquals(new BigDecimal("150.750"), result.value());
        
        // And - original balances should be unchanged (immutable)
        assertEquals(new BigDecimal("100.500"), balance1.value());
        assertEquals(new BigDecimal("50.250"), balance2.value());
    }

    @Test
    @DisplayName("Should subtract balances correctly")
    void shouldSubtractBalances() {
        // Given - two balances where the first is larger
        Balance balance1 = new Balance("100.500");
        Balance balance2 = new Balance("30.250");
        
        // When - subtracting
        Balance result = balance1.subtract(balance2);
        
        // Then - should return the correct difference
        assertEquals(new BigDecimal("70.250"), result.value());
        
        // And - original balances should be unchanged
        assertEquals(new BigDecimal("100.500"), balance1.value());
        assertEquals(new BigDecimal("30.250"), balance2.value());
    }

    @Test
    @DisplayName("Should compare balances correctly")
    void shouldCompareBalances() {
        // Given - balances with different amounts
        Balance small = new Balance("50.000");
        Balance medium = new Balance("100.000");
        Balance large = new Balance("150.000");
        Balance equalToMedium = new Balance("100.000");
        
        // Then - comparisons should work correctly
        assertTrue(small.isLessThan(medium));
        assertTrue(medium.isLessThan(large));
        assertFalse(medium.isLessThan(small));
        
        assertTrue(large.isGreaterThan(medium));
        assertTrue(medium.isGreaterThan(small));
        assertFalse(small.isGreaterThan(medium));
        
        assertTrue(medium.isEqualTo(equalToMedium));
        assertFalse(small.isEqualTo(medium));
        assertFalse(large.isEqualTo(medium));
    }

    @Test
    @DisplayName("Should reject negative amounts")
    void shouldRejectNegativeAmounts() {
        // Given - negative amounts
        BigDecimal negativeBigDecimal = new BigDecimal("-100.00");
        String negativeString = "-50.000";
        
        // When & Then - should throw for negative BigDecimal
        IllegalArgumentException bigDecimalException = assertThrows(
            IllegalArgumentException.class,
            () -> new Balance(negativeBigDecimal)
        );
        assertEquals("Balance cannot be negative", bigDecimalException.getMessage());
        
        // When & Then - should throw for negative string
        IllegalArgumentException stringException = assertThrows(
            IllegalArgumentException.class,
            () -> new Balance(negativeString)
        );
        assertEquals("Balance cannot be negative", stringException.getMessage());
    }

    @Test
    @DisplayName("Should reject null amounts")
    void shouldRejectNullAmounts() {
        // When & Then - should throw for null BigDecimal
        IllegalArgumentException bigDecimalException = assertThrows(
            IllegalArgumentException.class,
            () -> new Balance((BigDecimal) null)
        );
        assertEquals("Balance amount cannot be null", bigDecimalException.getMessage());
        
        // When & Then - should throw for null string
        IllegalArgumentException stringException = assertThrows(
            IllegalArgumentException.class,
            () -> new Balance((String) null)
        );
        assertEquals("Balance amount cannot be null", stringException.getMessage());
    }

    @Test
    @DisplayName("Should reject invalid string formats")
    void shouldRejectInvalidStringFormats() {
        // Given - invalid string formats
        String nonNumeric = "abc.def";
        String emptyString = "";
        String multipleDecimals = "100.50.25";
        
        // When & Then - should throw for non-numeric
        assertThrows(
            NumberFormatException.class,
            () -> new Balance(nonNumeric)
        );

        // When & Then - should throw for empty string
        assertThrows(
            NumberFormatException.class,
            () -> new Balance(emptyString)
        );
        
        // When & Then - should throw for multiple decimals
        assertThrows(
            NumberFormatException.class,
            () -> new Balance(multipleDecimals)
        );
    }

    @Test
    @DisplayName("Should prevent negative result from subtraction")
    void shouldPreventNegativeSubtraction() {
        // Given - balances where the second is larger
        Balance smaller = new Balance("50.000");
        Balance larger = new Balance("100.000");
        
        // When & Then - should throw when subtracting larger from smaller
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> smaller.subtract(larger)
        );
        assertEquals("Balance cannot be negative after subtraction", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject null in operations")
    void shouldRejectNullInOperations() {
        // Given - valid balance
        Balance balance = new Balance("100.000");
        
        // When & Then - should throw for null adding
        IllegalArgumentException addException = assertThrows(
            IllegalArgumentException.class,
            () -> balance.add(null)
        );
        assertEquals("Cannot add null balance", addException.getMessage());
        
        // When & Then - should throw for null subtracting
        IllegalArgumentException subtractException = assertThrows(
            IllegalArgumentException.class,
            () -> balance.subtract(null)
        );
        assertEquals("Cannot subtract null balance", subtractException.getMessage());
        
        // When & Then - should throw for null comparison
        IllegalArgumentException compareException = assertThrows(
            IllegalArgumentException.class,
            () -> balance.isLessThan(null)
        );
        assertEquals("Cannot compare with null balance", compareException.getMessage());
    }

    @Test
    @DisplayName("Should handle equals and hashCode correctly")
    void shouldHandleEqualsAndHashCode() {
        // Given - balances with same and different amounts
        Balance balance1 = new Balance("100.500");
        Balance balance2 = new Balance("100.500");
        Balance balance3 = new Balance("200.000");
        
        // Then - equal balances should be equal
        assertEquals(balance1, balance2);
        assertEquals(balance1.hashCode(), balance2.hashCode());
        
        // And - different balances should not be equal
        assertNotEquals(balance1, balance3);
        assertNotEquals(balance1.hashCode(), balance3.hashCode());
        
        // And - should not equal null or different types
        assertNotEquals(null, balance1);
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(new BigDecimal("100.500"), balance1);
    }

    @Test
    @DisplayName("Should handle precision edge cases")
    void shouldHandlePrecisionEdgeCases() {
        // Given - amounts with various precision challenges
        Balance fromLongDecimal = new Balance("100.123456789");
        Balance fromRoundingUp = new Balance("100.1235");
        Balance fromRoundingDown = new Balance("100.1234");
        
        // Then - should round to 3 decimal places using HALF_UP
        assertEquals(new BigDecimal("100.123"), fromLongDecimal.value());
        assertEquals(new BigDecimal("100.124"), fromRoundingUp.value());
        assertEquals(new BigDecimal("100.123"), fromRoundingDown.value());
    }

    @Test
    @DisplayName("Should be immutable")
    void shouldBeImmutable() {
        // Given - balance
        Balance original = new Balance("100.000");
        BigDecimal originalAmount = original.value();
        
        // When - performing operations
        original.add(new Balance("50.000"));
        original.subtract(new Balance("25.000"));
        original.isLessThan(new Balance("200.000"));
        
        // Then - original should remain unchanged
        assertEquals(originalAmount, original.value());
    }
} 