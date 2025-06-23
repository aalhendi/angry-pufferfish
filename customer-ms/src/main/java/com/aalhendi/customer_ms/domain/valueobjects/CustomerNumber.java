package com.aalhendi.customer_ms.domain.valueobjects;

import java.util.Objects;

/**
 * Customer number value object.
 * Represents a 7-digit customer number that uniquely identifies a customer.
 * TODO(aalhendi): move to common lib
 */
public record CustomerNumber(String value) {
    
    /**
     * Creates a CustomerNumber from a string value.
     */
    public CustomerNumber {
        if (Objects.isNull(value) || value.isEmpty()) {
            throw new IllegalArgumentException("Customer number cannot be null");
        }

        if (value.length() != 7) {
            throw new IllegalArgumentException("Customer number must be exactly 7 digits");
        }

        if (!value.matches("\\d{7}")) {
            throw new IllegalArgumentException("Customer number must contain only digits");
        }
    }
} 