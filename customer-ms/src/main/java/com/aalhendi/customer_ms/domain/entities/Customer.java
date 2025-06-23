package com.aalhendi.customer_ms.domain.entities;

import com.aalhendi.customer_ms.domain.valueobjects.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Customer domain entity.
 */
public final class Customer extends AbstractCustomer {

    private final Long id;

    /**
     * Private constructor for creating customers.
     */
    private Customer(Long id, CustomerNumber customerNumber, CustomerName name, NationalId nationalId,
                     CustomerType customerType, Address address, CustomerStatus status,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(customerNumber, name, nationalId, customerType, address, status, createdAt, updatedAt);
        this.id = Objects.requireNonNull(id, "ID cannot be null");
    }

    /**
     * Reconstitutes a customer from persistence (for loading from a database).
     */
    public static Customer reconstitute(Long id, CustomerNumber customerNumber, CustomerName name,
                                        NationalId nationalId, CustomerType customerType, Address address,
                                        CustomerStatus status, LocalDateTime createdAt,
                                        LocalDateTime updatedAt) {
        return new Customer(id, customerNumber, name, nationalId, customerType, address,
                status, createdAt, updatedAt);
    }

    /**
     * Get the ID of the customer.
     */
    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Customer[" +
                "id=" + id +
                ", customerNumber=" + getCustomerNumber() +
                ", name=" + getName() +
                ", nationalId=" + getNationalId() +
                ", customerType=" + getCustomerType() +
                ", address=" + getAddress() +
                ", status=" + getStatus() +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                ']';
    }
} 