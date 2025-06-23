package com.aalhendi.account_ms.domain.entities;

import com.aalhendi.account_ms.domain.valueobjects.AccountNumber;
import com.aalhendi.account_ms.domain.valueobjects.AccountStatus;
import com.aalhendi.account_ms.domain.valueobjects.Balance;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Account domain entity.
 */
public final class Account extends AbstractAccount {

    private final Long id;

    /**
     * Private constructor for creating accounts.
     */
    private Account(Long id, AccountNumber accountNumber, Balance balance, AccountStatus status,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(accountNumber, balance, status, createdAt, updatedAt);
        this.id = Objects.requireNonNull(id, "ID cannot be null");
    }

    /**
     * Reconstitutes an account from persistence (for loading from a database).
     */
    public static Account reconstitute(Long id, AccountNumber accountNumber, Balance balance,
                                       AccountStatus status, LocalDateTime createdAt,
                                       LocalDateTime updatedAt) {
        return new Account(id, accountNumber, balance, status, createdAt, updatedAt);
    }

    /**
     * Get the ID of the account.
     */
    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountNumber=" + getAccountNumber() +
                ", balance=" + getBalance() +
                ", status=" + getStatus() +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
} 