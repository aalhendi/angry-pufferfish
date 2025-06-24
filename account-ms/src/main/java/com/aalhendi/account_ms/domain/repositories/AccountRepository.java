package com.aalhendi.account_ms.domain.repositories;

import com.aalhendi.account_ms.domain.entities.Account;
import com.aalhendi.account_ms.domain.entities.NewAccount;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for Account entities.
 * Abstracts persistence layer details from the domain.
 */
public interface AccountRepository {

    /**
     * Saves a new account.
     *
     * @param newAccount the new account to save
     * @return the saved account with generated ID
     */
    Account save(NewAccount newAccount);

    /**
     * Updates an existing account.
     *
     * @param account the account to update
     * @return the updated account
     */
    Account save(Account account);

    /**
     * Finds an account by its account number.
     *
     * @param accountNumber the account number
     * @return the account if found, empty otherwise
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * Finds all accounts for a specific customer.
     *
     * @param customerNumber the 7-digit customer number
     * @return list of customer's accounts
     */
    List<Account> findByCustomerNumber(String customerNumber);

    /**
     * Counts the number of accounts for a specific customer.
     *
     * @param customerNumber the 7-digit customer number
     * @return the count of accounts
     */
    long countByCustomerNumber(String customerNumber);

    /**
     * Checks if an account exists by account number.
     *
     * @param accountNumber the account number
     * @return true if an account exists, false otherwise
     */
    boolean existsByAccountNumber(String accountNumber);

    /**
     * Soft deletes an account by marking it as deleted.
     *
     * @param accountNumber the account number to delete
     */
    void deleteByAccountNumber(String accountNumber);

    /**
     * Finds existing serial numbers for a customer.
     * Used for generating new account numbers.
     *
     * @param customerNumber the 7-digit customer number
     * @return list of existing serial numbers (last 3 digits)
     */
    List<String> findExistingSerialNumbers(String customerNumber);
} 