package com.aalhendi.account_ms.domain.valueobjects;

import java.util.Objects;

/**
 * Account number value object.
 * Represents a 10-digit account number with format CCCCCCCNNN
 * where CCCCCCC is a 7-digit customer number and NNN is 3-digit serial.
 */
public record AccountNumber(String value) {

    /**
     * Creates an AccountNumber from a string value.
     */
    public AccountNumber {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("Account number cannot be null");
        }

        if (value.length() != 10) {
            throw new IllegalArgumentException("Account number must be exactly 10 digits");
        }

        if (!value.matches("\\d{10}")) {
            throw new IllegalArgumentException("Account number must contain only digits");
        }
    }

    /**
     * Extracts the customer number (first 7 digits).
     * TODO(aalhendi): this should return a CustomerNumber value object...
     */
    public String customerNumber() {
        return value.substring(0, 7);
    }

    /**
     * Extracts the serial number (last 3 digits).
     * TODO(aalhendi): do we want a value object for SerialNumber?
     */
    public String serialNumber() {
        return value.substring(7, 10);
    }
} 