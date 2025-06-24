package com.aalhendi.customer_ms.domain.services;

import com.aalhendi.customer_ms.domain.entities.Customer;
import com.aalhendi.customer_ms.domain.entities.NewCustomer;
import com.aalhendi.customer_ms.domain.repositories.CustomerRepository;
import com.aalhendi.customer_ms.domain.valueobjects.*;
import com.aalhendi.customer_ms.domain.events.CustomerCreatedEvent;
import com.aalhendi.customer_ms.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of CustomerService.
 * Contains business logic for customer management operations.
 */
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final DomainEventPublisher eventPublisher;

    public CustomerServiceImpl(CustomerRepository customerRepository, DomainEventPublisher eventPublisher) {
        this.customerRepository = customerRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Customer createCustomer(String name, String nationalId, CustomerType customerType, String address) {
        String customerNumberValue = generateCustomerNumber();
        CustomerNumber customerNumber = new CustomerNumber(customerNumberValue);
        CustomerName customerName = new CustomerName(name);
        NationalId nationalIdVO = new NationalId(nationalId);
        Address addressVO = new Address(address);
        
        NewCustomer newCustomer = NewCustomer.create(
            customerNumber,
            customerName,
            nationalIdVO,
            customerType,
            addressVO
        );
        
        Customer savedCustomer = customerRepository.save(newCustomer);
        
        CustomerCreatedEvent event = new CustomerCreatedEvent(
            customerNumberValue,
            name,
            nationalId,
            customerType,
            address,
            savedCustomer.getStatus().name(),
            1L // Initial version
        );
        
        eventPublisher.publish(event);
        
        return savedCustomer;
    }

    /**
     * Generate a "unique" 7-digit number.
     * NOTE(aalhendi): this is just an attempt of sequential, unique ids
     */
    private String generateCustomerNumber() {
        long timestamp = System.currentTimeMillis() % 10000000; // Last 7 digits of timestamp
        return String.format("%07d", timestamp);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomer(String customerNumber) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findByCustomerNumber(String customerNumber) {
        return customerRepository.findByCustomerNumber(customerNumber);
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