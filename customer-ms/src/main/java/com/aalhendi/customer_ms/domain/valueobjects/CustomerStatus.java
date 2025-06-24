package com.aalhendi.customer_ms.domain.valueobjects;

import java.util.Objects;

/**
 * Customer status enumeration.
 * Represents the various states a customer can be in.
 */
public enum CustomerStatus {

    PENDING(0),
    ACTIVE(1),
    SUSPENDED(2),
    FROZEN(3),
    CLOSED(4);

    private final int code;

    CustomerStatus(int code) {
        this.code = code;
    }

    /**
     * Gets the integer code for this status.
     */
    public int getCode() {
        return code;
    }

    /**
     * Creates a CustomerStatus from an integer code.
     */
    public static CustomerStatus fromCode(int code) {
        for (CustomerStatus status : CustomerStatus.values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid customer status code: " + code);
    }

    /**
     * Creates a CustomerStatus from a string value.
     */
    public static CustomerStatus fromValue(String value) {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("Customer status value cannot be null");
        }

        String upperVal = value.trim().toUpperCase();
        try {
            return CustomerStatus.valueOf(upperVal);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid customer status: " + value);
        }
    }

    /**
     * Checks if this status allows operations.
     */
    public boolean allowsOperations() {
        return this == ACTIVE;
    }

    /**
     * Checks if this status is active.
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * Checks if this status is closed.
     */
    public boolean isClosed() {
        return this == CLOSED;
    }

    /**
     * Checks if this status is pending.
     */
    public boolean isPending() {
        return this == PENDING;
    }
} 