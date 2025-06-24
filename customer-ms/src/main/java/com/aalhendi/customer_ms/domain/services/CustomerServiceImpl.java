package com.aalhendi.customer_ms.domain.services;

import com.aalhendi.customer_ms.domain.entities.Customer;
import com.aalhendi.customer_ms.domain.entities.NewCustomer;
import com.aalhendi.customer_ms.domain.repositories.CustomerRepository;
import com.aalhendi.customer_ms.domain.valueobjects.*;
import com.aalhendi.customer_ms.domain.events.CustomerCreatedEvent;
import com.aalhendi.customer_ms.domain.events.CustomerUpdatedEvent;
import com.aalhendi.customer_ms.domain.events.CustomerStatusChangedEvent;
import com.aalhendi.customer_ms.domain.exceptions.BusinessException;
import com.aalhendi.customer_ms.domain.exceptions.CustomerError;
import com.aalhendi.customer_ms.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
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
        try {
            String customerNumberValue = generateCustomerNumber();
            CustomerNumber customerNumber = new CustomerNumber(customerNumberValue);
            CustomerName customerName = new CustomerName(name);
            
            NationalId nationalIdVO;
            // TODO(aalhendi): this is sloppy... but time is running out
            try {
                nationalIdVO = new NationalId(nationalId);
            } catch (IllegalArgumentException e) {
                throw new BusinessException(CustomerError.INVALID_NATIONAL_ID_FORMAT);
            }
            
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
                1L
            );
            
            eventPublisher.publish(event);
            
            return savedCustomer;
        } catch (IllegalArgumentException e) {
            // Handle value object validation errors
            String message = e.getMessage();
            if (message.contains("National ID")) {
                throw new BusinessException(CustomerError.INVALID_NATIONAL_ID_FORMAT);
            } else if (message.contains("name")) {
                throw new BusinessException(CustomerError.MISSING_REQUIRED_FIELD, "name");
            } else if (message.contains("address")) {
                throw new BusinessException(CustomerError.MISSING_REQUIRED_FIELD, "address");
            }
            // Re-throw if we don't know how to handle it
            throw e;
        }
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
        return customerRepository.findByCustomerNumber(customerNumber)
                .orElseThrow(() -> new BusinessException(
                    CustomerError.CUSTOMER_NOT_FOUND, customerNumber
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findByCustomerNumber(String customerNumber) {
        return customerRepository.findByCustomerNumber(customerNumber);
    }

    @Override
    public Customer updateCustomer(String customerNumber, String name, String address, CustomerType customerType) {
        Customer customer = getCustomer(customerNumber);
        
        StringBuilder changes = new StringBuilder();
        
        if (Objects.nonNull(name) && !name.trim().isEmpty()) {
            String oldName = customer.getName().value();
            customer.updateName(new CustomerName(name));
            if (!oldName.equals(name)) {
                changes.append("name: '").append(oldName).append("' -> '").append(name).append("'; ");
            }
        }
        
        if (Objects.nonNull(address) && !address.trim().isEmpty()) {
            String oldAddress = customer.getAddress().value();
            customer.updateAddress(new Address(address));
            if (!oldAddress.equals(address)) {
                changes.append("address: '").append(oldAddress).append("' -> '").append(address).append("'; ");
            }
        }
        
        if (Objects.nonNull(customerType)) {
            CustomerType oldType = customer.getCustomerType();
            customer.updateCustomerType(customerType);
            if (!oldType.equals(customerType)) {
                changes.append("type: '").append(oldType).append("' -> '").append(customerType).append("'; ");
            }
        }
        
        if (changes.length() == 0) {
            throw new BusinessException(
                CustomerError.MISSING_REQUIRED_FIELD, "at least one field to update"
            );
        }
        
        Customer updatedCustomer = customerRepository.save(customer);
        
        // Publish customer updated event
        CustomerUpdatedEvent event = new CustomerUpdatedEvent(
            updatedCustomer.getCustomerNumber().value(),
            updatedCustomer.getName().value(),
            updatedCustomer.getNationalId().value(),
            updatedCustomer.getCustomerType(),
            updatedCustomer.getAddress().value(),
            updatedCustomer.getStatus().name(),
            changes.toString().trim(),
            1L // TODO(aalhendi): implement proper versioning
        );
        
        eventPublisher.publish(event);
        
        return updatedCustomer;
    }

    @Override
    public Customer updateCustomerStatus(String customerNumber, CustomerStatus status) {
        Customer customer = getCustomer(customerNumber);
        
        CustomerStatus previousStatus = customer.getStatus();
        
        switch (status) {
            case ACTIVE -> customer.activate();
            case SUSPENDED -> customer.suspend();
            case FROZEN -> customer.freeze();
            case CLOSED -> customer.close();
        }
        
        Customer updatedCustomer = customerRepository.save(customer);
        
        // Publish customer status changed event
        CustomerStatusChangedEvent event = new CustomerStatusChangedEvent(
            updatedCustomer.getCustomerNumber().value(),
            previousStatus.name(),
            updatedCustomer.getStatus().name(),
            "Status updated via API",
            1L
        );
        
        eventPublisher.publish(event);
        
        return updatedCustomer;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> searchCustomersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException(
                CustomerError.MISSING_REQUIRED_FIELD, "name"
            );
        }
        
        return customerRepository.findByNameContainingIgnoreCase(name.trim());
    }
} 