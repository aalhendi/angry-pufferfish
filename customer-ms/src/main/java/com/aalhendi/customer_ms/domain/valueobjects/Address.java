package com.aalhendi.customer_ms.domain.valueobjects;

import java.util.Objects;

/**
 * Address value object.
 * Represents a customer's address with validation rules.
 */
public record Address(String value) {

    /**
     * Creates an Address from a string value.
     */
    public Address {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("Address cannot be null");
        }

        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty");
        }

        if (value.length() > 500) {
            throw new IllegalArgumentException("Address cannot exceed 500 characters");
        }

        // Normalize the value by trimming whitespace
        value = value.trim();
    }
} 