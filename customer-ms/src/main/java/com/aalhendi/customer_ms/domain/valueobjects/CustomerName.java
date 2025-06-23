package com.aalhendi.customer_ms.domain.valueobjects;

import java.util.Objects;

/**
 * Customer name value object.
 * Represents a customer's name with validation rules.
 */
public record CustomerName(String value) {

    /**
     * Creates a CustomerName from a string value.
     * NOTE(aalhendi): validating/parsing names is not a trivial thing.
     * Unicode, extended grapheme set, and other weird name edge cases can show up.
     * There's a cool article about this. We will impl basic methods for first and last name
     */
    public CustomerName {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("Customer name cannot be null");
        }

        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }

        if (value.length() > 255) {
            throw new IllegalArgumentException("Customer name cannot exceed 255 characters");
        }

        // Normalize the value by trimming whitespace
        value = value.trim();
    }

    /**
     * Gets the first name (first word).
     */
    public String firstName() {
        String[] parts = value.split("\\s+");
        return parts.length > 0 ? parts[0] : "";
    }

    /**
     * Gets the last name (remaining words after first).
     */
    public String lastName() {
        String[] parts = value.split("\\s+");
        if (parts.length <= 1) {
            return "";
        }
        return String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
    }
} 