package com.aalhendi.account_ms.domain.entities;

import com.aalhendi.account_ms.domain.valueobjects.AccountNumber;
import com.aalhendi.account_ms.domain.valueobjects.AccountStatus;
import com.aalhendi.account_ms.domain.valueobjects.Balance;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Account domain entity.
 * Core business entity representing a bank account with business rules and invariants.
 */
public abstract sealed class AbstractAccount permits Account, NewAccount {

    private final AccountNumber accountNumber;
    private Balance balance;
    private AccountStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Private constructor for creating accounts.
     */
    protected AbstractAccount(AccountNumber accountNumber, Balance balance, AccountStatus status,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.accountNumber = Objects.requireNonNull(accountNumber, "Account number cannot be null");
        this.balance = Objects.requireNonNull(balance, "Balance cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }

    public AccountNumber getAccountNumber() {
        return accountNumber;
    }

    public Balance getBalance() {
        return balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Credits the account with the specified amount.
     */
    public void credit(Balance amount) {
        validateTransactionPreconditions();
        if (Objects.isNull(amount)) {
            throw new IllegalArgumentException("Credit amount cannot be null");
        }

        this.balance = this.balance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Debits the account with the specified amount.
     */
    public void debit(Balance amount) {
        validateTransactionPreconditions();
        if (Objects.isNull(amount)) {
            throw new IllegalArgumentException("Debit amount cannot be null");
        }

        if (this.balance.isLessThan(amount)) {
            throw new IllegalStateException("Insufficient funds for debit operation");
        }

        this.balance = this.balance.subtract(amount);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Activates the account.
     */
    public void activate() {
        if (status == AccountStatus.CLOSED) {
            throw new IllegalStateException("Cannot activate a closed account");
        }

        this.status = AccountStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Suspends the account.
     */
    public void suspend() {
        if (status == AccountStatus.CLOSED) {
            throw new IllegalStateException("Cannot suspend a closed account");
        }

        this.status = AccountStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Closes the account.
     */
    public void close() {
        if (!balance.isEqualTo(Balance.ZERO)) {
            throw new IllegalStateException("Cannot close account with non-zero balance");
        }

        this.status = AccountStatus.CLOSED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Validates the preconditions for a transaction.
     */
    private void validateTransactionPreconditions() {
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account must be active to perform transactions");
        }
    }

    /**
     * Checks if the account is active.
     */
    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (Objects.isNull(o) || getClass() != o.getClass()) return false;
        AbstractAccount account = (AbstractAccount) o;
        return Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber=" + accountNumber +
                ", balance=" + balance +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 