package com.aalhendi.account_ms.domain.events;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Domain event published when an account's status is changed.
 */
public class AccountStatusChangedEvent extends AbstractDomainEvent {

    @JsonProperty("account_number")
    private final String accountNumber;

    @JsonProperty("customer_number")
    private final String customerNumber;

    @JsonProperty("previous_status")
    private final String previousStatus;

    @JsonProperty("new_status")
    private final String newStatus;

    @JsonProperty("reason")
    private final String reason;

    @JsonProperty("version")
    private final Long version;

    public AccountStatusChangedEvent(String accountNumber, String customerNumber,
                                   String previousStatus, String newStatus, 
                                   String reason, Long version) {
        super(accountNumber, "account.events.status-changed", version);
        this.accountNumber = accountNumber;
        this.customerNumber = customerNumber;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.reason = reason;
        this.version = version;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public String getReason() {
        return reason;
    }

    public Long getVersion() {
        return version;
    }
} 