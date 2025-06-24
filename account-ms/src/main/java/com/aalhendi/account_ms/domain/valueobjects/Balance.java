package com.aalhendi.account_ms.domain.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Balance value object for monetary amounts.
 * Uses BigDecimal for precision and enforces 3 decimal places.
 */
public record Balance(BigDecimal value) {

    /**
     * Creates a Balance from a BigDecimal.
     */
    public Balance {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("Balance amount cannot be null");
        }

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }

        value = value.setScale(3, RoundingMode.HALF_UP);
    }

    /**
     * Creates a Balance from a String.
     */
    public Balance(final String value) {
        this(parseAndValidate(value));
    }

    /**
     * Parses a string representation of a monetary value and validates it
     * NOTE(aalhendi): Java can't handle logic in constructors for records. Valhalla will fix this...surely!
     */
    private static BigDecimal parseAndValidate(String value) {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("Balance amount cannot be null");
        }
        return new BigDecimal(value);
    }

    /**
     * Creates a zero balance.
     */
    public static final Balance ZERO = new Balance(BigDecimal.ZERO);

    /**
     * Adds another balance to this one.
     */
    public Balance add(Balance other) {
        // NOTE(aalhendi): This is sad. The fact that I can sneak in a `null` balance in here is just sad.
        // The balance contents are checked to be non-null, but the balance object itself can *also* be null.
        if (Objects.isNull(other)) {
            throw new IllegalArgumentException("Cannot add null balance");
        }
        return new Balance(value.add(other.value));
    }

    /**
     * Subtracts another balance from this one.
     */
    public Balance subtract(Balance other) {
        if (Objects.isNull(other)) {
            throw new IllegalArgumentException("Cannot subtract null balance");
        }

        BigDecimal result = this.value.subtract(other.value);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance cannot be negative after subtraction");
        }

        return new Balance(result);
    }

    /**
     * Checks if this balance is less than another.
     */
    public boolean isLessThan(Balance other) {
        if (Objects.isNull(other)) {
            throw new IllegalArgumentException("Cannot compare with null balance");
        }
        return this.value.compareTo(other.value) < 0;
    }

    /**
     * Checks if this balance is greater than another.
     */
    public boolean isGreaterThan(Balance other) {
        if (Objects.isNull(other)) {
            throw new IllegalArgumentException("Cannot compare with null balance");
        }
        return this.value.compareTo(other.value) > 0;
    }

    /**
     * Checks if this balance equals another.
     */
    public boolean isEqualTo(Balance other) {
        if (Objects.isNull(other)) {
            return false;
        }
        return this.value.compareTo(other.value) == 0;
    }

    /**
     * Checks if this balance is less than or equal to another.
     */
    public boolean isLessThanOrEqualTo(Balance other) {
        if (Objects.isNull(other)) {
            throw new IllegalArgumentException("Cannot compare with null balance");
        }
        return this.value.compareTo(other.value) <= 0;
    }

    /**
     * Checks if this balance is greater than or equal to another.
     */
    public boolean isGreaterThanOrEqualTo(Balance other) {
        if (Objects.isNull(other)) {
            throw new IllegalArgumentException("Cannot compare with null balance");
        }
        return this.value.compareTo(other.value) >= 0;
    }
} 