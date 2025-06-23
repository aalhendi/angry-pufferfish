package com.aalhendi.customer_ms.domain.valueobjects;

/**
 * Customer type enumeration.
 * Represents the different types of customers supported by the system.
 */
public enum CustomerType {

    RETAIL("RETAIL"),
    CORPORATE("CORPORATE"),
    INVESTMENT("INVESTMENT");

    private final String value;

    CustomerType(String value) {
        this.value = value;
    }

    /**
     * Gets the string value for this customer type.
     */
    public String getValue() {
        return value;
    }

    /**
     * Creates a CustomerType from a string value.
     */
    public static CustomerType fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Customer type value cannot be null");
        }

        for (CustomerType type : CustomerType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid customer type: " + value);
    }

    /**
     * Checks if this customer type is retail.
     */
    public boolean isRetail() {
        return this == RETAIL;
    }

    /**
     * Checks if this customer type is investment.
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