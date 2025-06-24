package com.aalhendi.customer_ms.web.controllers;

import com.aalhendi.customer_ms.domain.entities.Customer;
import com.aalhendi.customer_ms.domain.services.CustomerService;
import com.aalhendi.customer_ms.domain.valueobjects.CustomerType;
import com.aalhendi.customer_ms.domain.valueobjects.CustomerStatus;
import com.aalhendi.customer_ms.domain.exceptions.BusinessException;
import com.aalhendi.customer_ms.domain.exceptions.CustomerError;
import com.aalhendi.customer_ms.web.dtos.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for customer operations.
 * Handles HTTP requests for customer management.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        try {
            CustomerType customerType = CustomerType.fromValue(request.getCustomerType());
            Customer customer = customerService.createCustomer(
                request.getName(),
                request.getNationalId(),
                customerType,
                request.getAddress()
            );
            
            CustomerResponse response = CustomerResponse.from(customer);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("customer_type") || e.getMessage().contains("CustomerType")) {
                throw new BusinessException(
                    CustomerError.INVALID_CUSTOMER_TYPE, request.getCustomerType()
                );
            }
            throw new BusinessException(
                CustomerError.VALIDATION_ERROR, request.getCustomerType(), "customer_type"
            );
        }
    }

    @GetMapping("/{customerNumber}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable String customerNumber) {
        if (Objects.isNull(customerNumber) || !customerNumber.matches("\\d{7}")) {
            throw new BusinessException(
                CustomerError.INVALID_CUSTOMER_NUMBER_FORMAT, customerNumber
            );
        }
        
        Optional<Customer> customerOpt = customerService.findByCustomerNumber(customerNumber);
        if (customerOpt.isEmpty()) {
            throw new BusinessException(
                CustomerError.CUSTOMER_NOT_FOUND, customerNumber
            );
        }
        
        CustomerResponse response = CustomerResponse.from(customerOpt.get());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{customerNumber}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable String customerNumber,
            @RequestBody UpdateCustomerRequest request) {
        
        if (Objects.isNull(request.getName()) && Objects.isNull(request.getAddress()) && Objects.isNull(request.getCustomerType())) {
            throw new BusinessException(
                CustomerError.NO_UPDATE_FIELDS_PROVIDED
            );
        }
        
        try {
            CustomerType customerType = request.getCustomerType() != null ? 
                CustomerType.fromValue(request.getCustomerType()) : null;
                
            Customer customer = customerService.updateCustomer(
                customerNumber,
                request.getName(),
                request.getAddress(),
                customerType
            );
            
            CustomerResponse response = CustomerResponse.from(customer);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                CustomerError.VALIDATION_ERROR, request.getCustomerType(), "customer_type"
            );
        }
    }

    @PutMapping("/{customerNumber}/status")
    public ResponseEntity<CustomerResponse> updateCustomerStatus(
            @PathVariable String customerNumber,
            @RequestBody UpdateCustomerStatusRequest request) {
        try {
            CustomerStatus status = CustomerStatus.fromValue(request.getStatus());
            Customer customer = customerService.updateCustomerStatus(customerNumber, status);
            CustomerResponse response = CustomerResponse.from(customer);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                CustomerError.VALIDATION_ERROR, request.getStatus(), "status"
            );
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponse>> searchCustomers(@RequestParam String name) {
        List<Customer> customers = customerService.searchCustomersByName(name);
        List<CustomerResponse> responses = customers.stream()
            .map(CustomerResponse::from)
            .toList();
        return ResponseEntity.ok(responses);
    }
} 