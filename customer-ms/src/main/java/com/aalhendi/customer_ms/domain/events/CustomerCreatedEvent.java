package com.aalhendi.customer_ms.domain.events;

import com.aalhendi.customer_ms.domain.valueobjects.CustomerType;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Domain event published when a customer is created.
 * This event notifies other services about the new customer creation.
 */
public class CustomerCreatedEvent extends AbstractDomainEvent {
    
    public static final String EVENT_TYPE = "CustomerCreated";
    
    @JsonProperty("customer_number")
    private final String customerNumber;
    
    @JsonProperty("name")
    private final String name;
    
    @JsonProperty("national_id")
    private final String nationalId;
    
    @JsonProperty("customer_type")
    private final String customerType;
    
    @JsonProperty("address")
    private final String address;
    
    @JsonProperty("status")
    private final String status;
    
    public CustomerCreatedEvent(String customerNumber, String name, String nationalId, 
                               CustomerType customerType, String address, String status,
                               Long aggregateVersion) {
        super(customerNumber, EVENT_TYPE, aggregateVersion);
        this.customerNumber = customerNumber;
        this.name = name;
        this.nationalId = nationalId;
        this.customerType = customerType.name();
        this.address = address;
        this.status = status;
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
    
    public String getCustomerType() {
        return customerType;
    }
    
    public String getAddress() {
        return address;
    }
    
    public String getStatus() {
        return status;
    }
} 