package com.aalhendi.customer_ms.domain.services;

import com.aalhendi.customer_ms.domain.entities.Customer;
import com.aalhendi.customer_ms.domain.valueobjects.CustomerStatus;
import com.aalhendi.customer_ms.domain.valueobjects.CustomerType;

import java.util.List;
import java.util.Optional;

/**
 * Domain service for customer operations.
 * Contains business logic for customer management.
 */
public interface CustomerService {

    /**
     * Creates a new customer with the provided details.
     */
    Customer createCustomer(String name, String nationalId, CustomerType customerType, String address);

    /**
     * Retrieves a customer by their customer number.
     */
    Customer getCustomer(String customerNumber);

    /**
     * Finds a customer by their customer number.
     */
    Optional<Customer> findByCustomerNumber(String customerNumber);

    /**
     * Updates customer information.
     */
    Customer updateCustomer(String customerNumber, String name, String address, CustomerType customerType);

    /**
     * Updates customer status.
     */
    Customer updateCustomerStatus(String customerNumber, CustomerStatus status);

    /**
     * Searches customers by name.
     */
    List<Customer> searchCustomersByName(String name);
} 