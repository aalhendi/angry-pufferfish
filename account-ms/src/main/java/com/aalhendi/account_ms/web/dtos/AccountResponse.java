package com.aalhendi.account_ms.web.dtos;

import com.aalhendi.account_ms.domain.entities.Account;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Response DTO for account data.
 */
public class AccountResponse {

    @JsonProperty("account_number")
    private String accountNumber;

    @JsonProperty("customer_number")
    private String customerNumber;

    @JsonProperty("account_type")
    private String accountType;

    @JsonProperty("balance")
    private String balance;

    @JsonProperty("status")
    private String status;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public AccountResponse() {
    }

    public AccountResponse(String accountNumber, String customerNumber, String accountType,
                           String balance, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.accountNumber = accountNumber;
        this.customerNumber = customerNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Creates an AccountResponse from a domain Account entity.
     */
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getAccountNumber().value(),
                account.getAccountNumber().customerNumber(),
                account.getAccountType().toString(),
                account.getBalance().value().toString(),
                account.getStatus().toString(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }

    // Getters and setters
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 