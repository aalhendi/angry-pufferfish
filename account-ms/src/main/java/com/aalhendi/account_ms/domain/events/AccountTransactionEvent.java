package com.aalhendi.account_ms.domain.events;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain event published when a transaction occurs on an account.
 */
public class AccountTransactionEvent extends AbstractDomainEvent {

    @JsonProperty("account_number")
    private final String accountNumber;

    @JsonProperty("customer_number")
    private final String customerNumber;

    @JsonProperty("transaction_type")
    private final String transactionType; // CREDIT or DEBIT

    @JsonProperty("amount")
    private final BigDecimal amount;

    @JsonProperty("previous_balance")
    private final BigDecimal previousBalance;

    @JsonProperty("new_balance")
    private final BigDecimal newBalance;

    @JsonProperty("description")
    private final String description;

    @JsonProperty("version")
    private final Long version;

    public AccountTransactionEvent(String accountNumber, String customerNumber,
                                 String transactionType, BigDecimal amount,
                                 BigDecimal previousBalance, BigDecimal newBalance,
                                 String description, Long version) {
        super(accountNumber, "account.events.transaction", version);
        this.accountNumber = accountNumber;
        this.customerNumber = customerNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.previousBalance = previousBalance;
        this.newBalance = newBalance;
        this.description = description;
        this.version = version;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getPreviousBalance() {
        return previousBalance;
    }

    public BigDecimal getNewBalance() {
        return newBalance;
    }

    public String getDescription() {
        return description;
    }

    public Long getVersion() {
        return version;
    }
} 