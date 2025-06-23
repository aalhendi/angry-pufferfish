package com.aalhendi.customer_ms.domain.valueobjects;

import java.util.Objects;

/**
 * National ID value object.
 * Represents a 12-digit national identification number with format validation.
 */
public record NationalId(String value) {

    /**
     * Creates a NationalId from a string value.
     */
    public NationalId {
        if (Objects.isNull(value) || value.isEmpty()) {
            throw new IllegalArgumentException("National ID cannot be null");
        }

        if (value.length() != 12) {
            throw new IllegalArgumentException("National ID must be exactly 12 digits");
        }

        if (!value.matches("\\d{12}")) {
            throw new IllegalArgumentException("National ID must contain only digits");
        }

        // Basic format validation - the first digit should be 1-4 (nationality code)
        char firstDigit = value.charAt(0);
        if (firstDigit < '1' || firstDigit > '4') {
            throw new IllegalArgumentException("National ID must start with nationality code (1-4)");
        }

        // TODO(aalhendi): Theres a hash-check on the last few digits... ill try to find it and put it in here as well
    }

    /**
     * Gets the nationality code (first digit).
     */
    public String nationalityCode() {
        return value.substring(0, 1);
    }

    /**
     * Gets the identification number (remaining 11 digits).
     */
    public String identificationNumber() {
        return value.substring(1, 12);
    }
} 