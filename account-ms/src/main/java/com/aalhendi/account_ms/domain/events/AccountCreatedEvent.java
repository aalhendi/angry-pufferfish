package com.aalhendi.account_ms.domain.events;

import com.aalhendi.account_ms.domain.valueobjects.AccountType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Domain event published when an account is created.
 * This event notifies other services about the new account creation.
 */
public class AccountCreatedEvent extends AbstractDomainEvent {
    
    public static final String EVENT_TYPE = "AccountCreated";
    
    @JsonProperty("account_number")
    private final String accountNumber;
    
    @JsonProperty("customer_number")
    private final String customerNumber;
    
    @JsonProperty("account_type")
    private final String accountType;
    
    @JsonProperty("initial_balance")
    private final BigDecimal initialBalance;
    
    @JsonProperty("status")
    private final String status;
    
    public AccountCreatedEvent(String accountNumber, String customerNumber, AccountType accountType, 
                             BigDecimal initialBalance, String status, Long aggregateVersion) {
        super(accountNumber, EVENT_TYPE, aggregateVersion);
        this.accountNumber = accountNumber;
        this.customerNumber = customerNumber;
        this.accountType = accountType.name();
        this.initialBalance = initialBalance;
        this.status = status;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public String getCustomerNumber() {
        return customerNumber;
    }
    
    public String getAccountType() {
        return accountType;
    }
    
    public BigDecimal getInitialBalance() {
        return initialBalance;
    }
    
    public String getStatus() {
        return status;
    }
} 