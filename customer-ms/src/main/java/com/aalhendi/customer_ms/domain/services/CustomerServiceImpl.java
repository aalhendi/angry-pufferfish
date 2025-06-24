package com.aalhendi.customer_ms.domain.services;

import com.aalhendi.customer_ms.domain.entities.Customer;
import com.aalhendi.customer_ms.domain.repositories.CustomerRepository;
import com.aalhendi.customer_ms.domain.valueobjects.CustomerStatus;
import com.aalhendi.customer_ms.domain.valueobjects.CustomerType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of CustomerService.
 * Contains business logic for customer management operations.
 */
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer createCustomer(String name, String nationalId, CustomerType customerType, String address) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomer(String customerNumber) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Customer updateCustomer(String customerNumber, String name, String address, CustomerType customerType) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Customer updateCustomerStatus(String customerNumber, CustomerStatus status) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> searchCustomersByName(String name) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
} 