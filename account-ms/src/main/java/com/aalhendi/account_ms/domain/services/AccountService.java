package com.aalhendi.account_ms.domain.services;

import com.aalhendi.account_ms.domain.entities.Account;
import com.aalhendi.account_ms.domain.valueobjects.AccountStatus;
import com.aalhendi.account_ms.domain.valueobjects.AccountType;
import com.aalhendi.account_ms.domain.valueobjects.Balance;

import java.util.List;
import java.util.Optional;

/**
 * Domain service for account operations.
 * Contains business logic for account management.
 */
public interface AccountService {

    /**
     * Creates a new account for a customer.
     *
     * @param customerNumber the 7-digit customer number
     * @param accountType    the type of account to create
     * @return the created account
     * @throws IllegalArgumentException if a customer doesn't exist or has reached the account limit
     */
    Account createAccount(String customerNumber, AccountType accountType);

    /**
     * Retrieves an account by its account number.
     *
     * @param accountNumber the 10-digit account number
     * @return the account if found, empty otherwise
     */
    Optional<Account> getAccount(String accountNumber);

    /**
     * Retrieves all accounts for a customer.
     *
     * @param customerNumber the 7-digit customer number
     * @return list of customer's accounts
     */
    List<Account> getAccountsByCustomer(String customerNumber);

    /**
     * Updates account status.
     *
     * @param accountNumber the account number
     * @param status        the new status
     * @return the updated account
     * @throws IllegalArgumentException if an account doesn't exist or status transition is invalid
     */
    Account updateAccountStatus(String accountNumber, AccountStatus status);

    /**
     * Credits money to an account.
     *
     * @param accountNumber the account number
     * @param amount        the amount to credit
     * @return the updated account
     * @throws IllegalArgumentException if an account doesn't exist or is not active
     */
    Account creditAccount(String accountNumber, Balance amount);

    /**
     * Debits money from an account.
     *
     * @param accountNumber the account number
     * @param amount        the amount to debit
     * @return the updated account
     * @throws IllegalArgumentException if the account doesn't exist, is not active, or has insufficient funds
     */
    Account debitAccount(String accountNumber, Balance amount);

    /**
     * Soft deletes an account (marks as closed).
     *
     * @param accountNumber the account number to close
     * @throws IllegalArgumentException if an account doesn't exist or cannot be closed
     */
    void closeAccount(String accountNumber);
} 