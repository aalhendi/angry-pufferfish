package com.aalhendi.customer_ms.web.controllers;

import com.aalhendi.customer_ms.domain.services.CustomerService;
import com.aalhendi.customer_ms.web.dtos.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CreateCustomerRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping("/{customerNumber}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable String customerNumber) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PutMapping("/{customerNumber}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable String customerNumber,
            @RequestBody UpdateCustomerRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PutMapping("/{customerNumber}/status")
    public ResponseEntity<CustomerResponse> updateCustomerStatus(
            @PathVariable String customerNumber,
            @RequestBody UpdateCustomerStatusRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponse>> searchCustomers(@RequestParam String name) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
} 