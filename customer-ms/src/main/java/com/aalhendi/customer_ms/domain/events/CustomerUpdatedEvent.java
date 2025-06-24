package com.aalhendi.customer_ms.domain.events;

import com.aalhendi.customer_ms.domain.valueobjects.CustomerType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Domain event published when a customer is updated.
 */
public class CustomerUpdatedEvent extends AbstractDomainEvent {

    @JsonProperty("customer_number")
    private final String customerNumber;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("national_id")
    private final String nationalId;

    @JsonProperty("customer_type")
    private final CustomerType customerType;

    @JsonProperty("address")
    private final String address;

    @JsonProperty("status")
    private final String status;

    @JsonProperty("version")
    private final Long version;

    @JsonProperty("changes")
    private final String changes; // Description of what changed

    public CustomerUpdatedEvent(String customerNumber, String name, String nationalId,
                               CustomerType customerType, String address, String status,
                               String changes, Long version) {
        super(customerNumber, "customer.events.updated", version);
        this.customerNumber = customerNumber;
        this.name = name;
        this.nationalId = nationalId;
        this.customerType = customerType;
        this.address = address;
        this.status = status;
        this.changes = changes;
        this.version = version;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public String getName() {
        return name;
    }

    public String getNationalId() {
        return nationalId;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public String getAddress() {
        return address;
    }

    public String getStatus() {
        return status;
    }

    public String getChanges() {
        return changes;
    }

    public Long getVersion() {
        return version;
    }
} 