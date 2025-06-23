package com.aalhendi.customer_ms.domain.entities;

import com.aalhendi.customer_ms.domain.valueobjects.*;

import java.time.LocalDateTime;

/**
 * NewCustomer domain entity.
 */
public final class NewCustomer extends AbstractCustomer {

    /**
     * Private constructor for creating customers.
     */
    private NewCustomer(CustomerNumber customerNumber, CustomerName name, NationalId nationalId,
                        CustomerType customerType, Address address, CustomerStatus status,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(customerNumber, name, nationalId, customerType, address, status, createdAt, updatedAt);
    }

    /**
     * Creates a new customer (for new customer creation).
     */
    public static NewCustomer create(CustomerNumber customerNumber, CustomerName name,
                                     NationalId nationalId, CustomerType customerType, Address address) {
        LocalDateTime now = LocalDateTime.now();
        return new NewCustomer(customerNumber, name, nationalId, customerType, address,
                CustomerStatus.PENDING, now, now);
    }
} 