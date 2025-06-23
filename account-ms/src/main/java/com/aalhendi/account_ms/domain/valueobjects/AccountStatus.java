package com.aalhendi.account_ms.domain.valueobjects;

/**
 * Account status enumeration.
 * Represents the various states an account can be in.
 */
public enum AccountStatus {

    PENDING(0),
    ACTIVE(1),
    SUSPENDED(2),
    FROZEN(3),
    CLOSED(4);

    private final int code;

    AccountStatus(int code) {
        this.code = code;
    }

    /**
     * Gets the integer code for this status.
     */
    public int getCode() {
        return code;
    }

    /**
     * Creates an AccountStatus from an integer code.
     */
    public static AccountStatus fromCode(int code) {
        for (AccountStatus status : AccountStatus.values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid account status code: " + code);
    }

    /**
     * Checks if this status allows transactions.
     */
    public boolean allowsTransactions() {
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
} 