package com.aalhendi.account_ms.domain.valueobjects;

import java.util.Objects;

/**
 * Account type enumeration.
 * Defines the different categories of accounts.
 */
public enum AccountType {

    SAVING,
    INVESTMENT,
    SALARY;

    /**
     * Creates an AccountType from a string value.
     */
    public static AccountType fromString(String value) {
        if (Objects.isNull(value)) {
            throw new NullPointerException("Account type cannot be null");
        }

        String upperValue = value.trim().toUpperCase();

        try {
            return AccountType.valueOf(upperValue);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("Invalid account type: %s. Valid types are: SAVING, INVESTMENT, SALARY", value)
            );
        }
    }

    /**
     * Checks if this account type is a salary account.
     */
    public boolean isSalary() {
        return this == SALARY;
    }

}