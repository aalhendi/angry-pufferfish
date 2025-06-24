package com.aalhendi.account_ms.web.controllers;

import com.aalhendi.account_ms.domain.services.AccountService;
import com.aalhendi.account_ms.domain.entities.Account;
import com.aalhendi.account_ms.domain.valueobjects.AccountType;
import com.aalhendi.account_ms.domain.valueobjects.AccountStatus;
import com.aalhendi.account_ms.domain.valueobjects.Balance;
import com.aalhendi.account_ms.domain.exceptions.BusinessException;
import com.aalhendi.account_ms.domain.exceptions.AccountError;
import com.aalhendi.account_ms.web.dtos.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountType accountType = AccountType.fromString(request.getAccountType());
        Account account = accountService.createAccount(request.getCustomerNumber(), accountType);
        AccountResponse response = AccountResponse.from(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves an account by its account number.
     *
     * @param accountNumber the 10-digit account number
     * @return the account if found, or HTTP 404 if not found
     */
    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountNumber) {
        if (Objects.isNull(accountNumber) || !accountNumber.matches("\\d{10}")) {
            throw new BusinessException(
                AccountError.VALIDATION_ERROR, accountNumber, "account_number"
            );
        }
        
        Optional<Account> accountOpt = accountService.getAccount(accountNumber);
        if (accountOpt.isEmpty()) {
            throw new BusinessException(
                AccountError.ACCOUNT_NOT_FOUND, accountNumber
            );
        }
        
        AccountResponse response = AccountResponse.from(accountOpt.get());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all accounts for a specific customer.
     *
     * @param customerNumber the 7-digit customer number
     * @return list of customer's accounts
     */
    @GetMapping("/customer/{customerNumber}")
    public ResponseEntity<List<AccountResponse>> getAccountsByCustomer(@PathVariable String customerNumber) {
        List<Account> accounts = accountService.getAccountsByCustomer(customerNumber);
        List<AccountResponse> responses = accounts.stream()
            .map(AccountResponse::from)
            .toList();
        return ResponseEntity.ok(responses);
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
        
        AccountStatus status;
        try {
            status = AccountStatus.fromValue(request.getStatus());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                AccountError.VALIDATION_ERROR, request.getStatus(), "status"
            );
        }
        
        Account account = accountService.updateAccountStatus(accountNumber, status);
        AccountResponse response = AccountResponse.from(account);
        return ResponseEntity.ok(response);
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
            @Valid @RequestBody TransactionRequest request) {
        
        Balance amount = new Balance(request.getAmount());
        Account account = accountService.creditAccount(accountNumber, amount);
        AccountResponse response = AccountResponse.from(account);
        return ResponseEntity.ok(response);
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
            @Valid @RequestBody TransactionRequest request) {
        
        Balance amount = new Balance(request.getAmount());
        Account account = accountService.debitAccount(accountNumber, amount);
        AccountResponse response = AccountResponse.from(account);
        return ResponseEntity.ok(response);
    }

    /**
     * Closes an account (soft delete).
     *
     * @param accountNumber the account number
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> closeAccount(@PathVariable String accountNumber) {
        accountService.closeAccount(accountNumber);
        return ResponseEntity.noContent().build();
    }
} 