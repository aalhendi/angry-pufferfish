package com.aalhendi.account_ms.web.controllers;

import com.aalhendi.account_ms.domain.services.AccountService;
import com.aalhendi.account_ms.web.dtos.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for account management operations.
 * Provides endpoints for creating, retrieving, updating, and managing accounts.
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Creates a new account for a customer.
     *
     * @param request the account creation request
     * @return the created account with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody CreateAccountRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Retrieves an account by its account number.
     *
     * @param accountNumber the 10-digit account number
     * @return the account if found, or HTTP 404 if not found
     */
    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountNumber) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Retrieves all accounts for a specific customer.
     *
     * @param customerNumber the 7-digit customer number
     * @return list of customer's accounts
     */
    @GetMapping("/customer/{customerNumber}")
    public ResponseEntity<List<AccountResponse>> getAccountsByCustomer(@PathVariable String customerNumber) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Updates an account's status.
     *
     * @param accountNumber the account number
     * @param request       the status update request
     * @return the updated account
     */
    @PutMapping("/{accountNumber}/status")
    public ResponseEntity<AccountResponse> updateAccountStatus(
            @PathVariable String accountNumber,
            @RequestBody UpdateAccountStatusRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Credits money to an account.
     *
     * @param accountNumber the account number
     * @param request       the transaction request
     * @return the updated account
     */
    @PostMapping("/{accountNumber}/credit")
    public ResponseEntity<AccountResponse> creditAccount(
            @PathVariable String accountNumber,
            @RequestBody TransactionRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Debits money from an account.
     *
     * @param accountNumber the account number
     * @param request       the transaction request
     * @return the updated account
     */
    @PostMapping("/{accountNumber}/debit")
    public ResponseEntity<AccountResponse> debitAccount(
            @PathVariable String accountNumber,
            @RequestBody TransactionRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Closes an account (soft delete).
     *
     * @param accountNumber the account number
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> closeAccount(@PathVariable String accountNumber) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
} 