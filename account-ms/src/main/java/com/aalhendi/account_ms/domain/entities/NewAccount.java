package com.aalhendi.account_ms.domain.entities;

import com.aalhendi.account_ms.domain.valueobjects.AccountNumber;
import com.aalhendi.account_ms.domain.valueobjects.AccountStatus;
import com.aalhendi.account_ms.domain.valueobjects.Balance;

import java.time.LocalDateTime;

/**
 * NewAccount domain entity.
 */
public final class NewAccount extends AbstractAccount {

    /**
     * Private constructor for creating accounts.
     */
    private NewAccount(AccountNumber accountNumber, Balance balance, AccountStatus status,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(accountNumber, balance, status, createdAt, updatedAt);
    }

    /**
     * Creates a new account (for new account creation).
     */
    public static NewAccount create(AccountNumber accountNumber) {
        LocalDateTime now = LocalDateTime.now();
        return new NewAccount(accountNumber, Balance.ZERO, AccountStatus.PENDING, now, now);
    }
} 