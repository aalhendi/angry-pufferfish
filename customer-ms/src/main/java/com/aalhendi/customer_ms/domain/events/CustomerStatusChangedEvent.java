package com.aalhendi.customer_ms.domain.events;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Domain event published when a customer's status is changed.
 */
public class CustomerStatusChangedEvent extends AbstractDomainEvent {

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

    public CustomerStatusChangedEvent(String customerNumber, String previousStatus, 
                                    String newStatus, String reason, Long version) {
        super(customerNumber, "customer.events.status-changed", version);
        this.customerNumber = customerNumber;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.reason = reason;
        this.version = version;
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