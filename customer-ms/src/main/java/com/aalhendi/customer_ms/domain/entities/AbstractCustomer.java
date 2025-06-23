package com.aalhendi.customer_ms.domain.entities;

import com.aalhendi.customer_ms.domain.valueobjects.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Customer domain entity.
 * Core business entity representing a bank customer with business rules and invariants.
 */
public abstract sealed class AbstractCustomer permits Customer, NewCustomer {

    private final CustomerNumber customerNumber;
    private CustomerName name;
    private final NationalId nationalId;
    private CustomerType customerType;
    private Address address;
    private CustomerStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Protected constructor for creating customers.
     */
    protected AbstractCustomer(CustomerNumber customerNumber, CustomerName name, NationalId nationalId,
                               CustomerType customerType, Address address, CustomerStatus status,
                               LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.customerNumber = Objects.requireNonNull(customerNumber, "Customer number cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.nationalId = Objects.requireNonNull(nationalId, "National ID cannot be null");
        this.customerType = Objects.requireNonNull(customerType, "Customer type cannot be null");
        this.address = Objects.requireNonNull(address, "Address cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }

    public CustomerNumber getCustomerNumber() {
        return customerNumber;
    }

    public CustomerName getName() {
        return name;
    }

    public NationalId getNationalId() {
        return nationalId;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public Address getAddress() {
        return address;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Updates the customer's name.
     */
    public void updateName(CustomerName newName) {
        validateOperationPreconditions();
        if (Objects.isNull(newName)) {
            throw new IllegalArgumentException("Name cannot be null");
        }

        this.name = newName;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the customer's address.
     */
    public void updateAddress(Address newAddress) {
        validateOperationPreconditions();
        if (Objects.isNull(newAddress)) {
            throw new IllegalArgumentException("Address cannot be null");
        }

        this.address = newAddress;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the customer's type.
     */
    public void updateCustomerType(CustomerType newType) {
        validateOperationPreconditions();
        if (Objects.isNull(newType)) {
            throw new IllegalArgumentException("Customer type cannot be null");
        }

        this.customerType = newType;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Activates the customer.
     */
    public void activate() {
        if (status.isClosed()) {
            throw new IllegalStateException("Cannot activate a closed customer");
        }

        this.status = CustomerStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Suspends the customer.
     */
    public void suspend() {
        if (status.isClosed()) {
            throw new IllegalStateException("Cannot suspend a closed customer");
        }

        this.status = CustomerStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Freezes the customer.
     */
    public void freeze() {
        if (status.isClosed()) {
            throw new IllegalStateException("Cannot freeze a closed customer");
        }

        this.status = CustomerStatus.FROZEN;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Closes the customer.
     */
    public void close() {
        this.status = CustomerStatus.CLOSED;
        this.updatedAt = LocalDateTime.now();
    }


    /**
     * Validates the preconditions for an operation.
     */
    private void validateOperationPreconditions() {
        if (status.isClosed()) {
            throw new IllegalStateException("Cannot perform operations on a closed customer");
        }
    }

    /**
     * Checks if the customer is active.
     */
    public boolean isActive() {
        return status.isActive();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (Objects.isNull(o) || getClass() != o.getClass()) return false;
        AbstractCustomer customer = (AbstractCustomer) o;
        return Objects.equals(customerNumber, customer.customerNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerNumber);
    }

    @Override
    public String toString() {
        return "Customer[" +
                "customerNumber=" + customerNumber +
                ", name=" + name +
                ", nationalId=" + nationalId +
                ", customerType=" + customerType +
                ", address=" + address +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ']';
    }
} 