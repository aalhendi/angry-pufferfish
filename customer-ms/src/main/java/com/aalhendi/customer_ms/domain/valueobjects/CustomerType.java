package com.aalhendi.customer_ms.domain.valueobjects;

/**
 * Customer type enumeration.
 * Represents the different types of customers supported by the system.
 */
public enum CustomerType {

    RETAIL,
    CORPORATE,
    INVESTMENT;

    /**
     * Creates a CustomerType from a string value.
     */
    public static CustomerType fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Customer type value cannot be null");
        }

        String upperVal = value.trim().toUpperCase();
        try {
            return CustomerType.valueOf(upperVal);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid customer type: " + value);
        }
    }

    /**
     * Checks if this customer type is retail.
     */
    public boolean isRetail() {
        return this == RETAIL;
    }

    /**
     * Checks if this customer type is an investment.
     */
    public boolean isInvestment() {
        return this == INVESTMENT;
    }

    /**
     * Checks if this customer type is corporate.
     */
    public boolean isCorporate() {
        return this == CORPORATE;
    }
} 