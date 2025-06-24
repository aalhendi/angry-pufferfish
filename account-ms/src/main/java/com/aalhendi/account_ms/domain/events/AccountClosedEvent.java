package com.aalhendi.account_ms.domain.events;

import com.aalhendi.account_ms.domain.valueobjects.AccountType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Domain event published when an account is closed.
 */
public class AccountClosedEvent extends AbstractDomainEvent {

    @JsonProperty("account_number")
    private final String accountNumber;

    @JsonProperty("customer_number")
    private final String customerNumber;

    @JsonProperty("account_type")
    private final AccountType accountType;

    @JsonProperty("closure_reason")
    private final String closureReason;

    @JsonProperty("closed_at")
    private final LocalDateTime closedAt;

    @JsonProperty("version")
    private final Long version;

    public AccountClosedEvent(String accountNumber, String customerNumber,
                            AccountType accountType, String closureReason, Long version) {
        super(accountNumber, "account.events.closed", version);
        this.accountNumber = accountNumber;
        this.customerNumber = customerNumber;
        this.accountType = accountType;
        this.closureReason = closureReason;
        this.closedAt = LocalDateTime.now();
        this.version = version;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public String getClosureReason() {
        return closureReason;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public Long getVersion() {
        return version;
    }
} 