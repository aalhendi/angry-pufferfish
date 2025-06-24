package com.aalhendi.customer_ms.domain.repositories;

import com.aalhendi.customer_ms.domain.entities.Customer;
import com.aalhendi.customer_ms.domain.entities.NewCustomer;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for Customer entities.
 * Abstracts persistence layer details from the domain.
 */
public interface CustomerRepository {

    /**
     * Saves a new customer.
     *
     * @param newCustomer the new customer to save
     * @return the saved customer with generated ID
     */
    Customer save(NewCustomer newCustomer);

    /**
     * Updates an existing customer.
     *
     * @param customer the customer to update
     * @return the updated customer
     */
    Customer save(Customer customer);

    /**
     * Finds a customer by customer number.
     *
     * @param customerNumber the 7-digit customer number
     * @return the customer if found, empty otherwise
     */
    Optional<Customer> findByCustomerNumber(String customerNumber);

    /**
     * Finds a customer by national ID.
     *
     * @param nationalId the 12-digit national ID
     * @return the customer if found, empty otherwise
     */
    Optional<Customer> findByNationalId(String nationalId);

    /**
     * Searches customers by name (case-insensitive partial match).
     *
     * @param name the name to search for
     * @return list of matching customers
     */
    List<Customer> findByNameContaining(String name);

    /**
     * Searches customers by name (case-insensitive partial match).
     *
     * @param name the name to search for
     * @return list of matching customers
     */
    List<Customer> findByNameContainingIgnoreCase(String name);

    /**
     * Checks if a customer exists by customer number.
     *
     * @param customerNumber the customer number
     * @return true if customer exists, false otherwise
     */
    boolean existsByCustomerNumber(String customerNumber);

    /**
     * Checks if a customer exists by national ID.
     *
     * @param nationalId the national ID
     * @return true if customer exists, false otherwise
     */
    boolean existsByNationalId(String nationalId);

    /**
     * Soft deletes a customer by marking it as deleted.
     *
     * @param customerNumber the customer number to delete
     */
    void deleteByCustomerNumber(String customerNumber);
} 