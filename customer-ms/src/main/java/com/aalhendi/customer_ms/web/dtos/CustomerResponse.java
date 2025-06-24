package com.aalhendi.customer_ms.web.dtos;

import com.aalhendi.customer_ms.domain.entities.Customer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Response DTO for customer data.
 * Contains customer information returned by the API.
 */
public class CustomerResponse {

    @JsonProperty("customer_number")
    private String customerNumber;

    @JsonProperty("name")
    private String name;

    @JsonProperty("national_id")
    private String nationalId;

    @JsonProperty("customer_type")
    private String customerType;

    @JsonProperty("address")
    private String address;

    @JsonProperty("status")
    private String status;

    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * Default constructor for JSON serialization.
     */
    public CustomerResponse() {
    }

    /**
     * Constructor with all fields.
     */
    public CustomerResponse(String customerNumber, String name, String nationalId,
                            String customerType, String address, String status,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.customerNumber = customerNumber;
        this.name = name;
        this.nationalId = nationalId;
        this.customerType = customerType;
        this.address = address;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Creates a CustomerResponse from a domain Customer entity.
     */
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getCustomerNumber().value(),
                customer.getName().value(),
                customer.getNationalId().value(),
                customer.getCustomerType().name(),
                customer.getAddress().value(),
                customer.getStatus().name(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }

    // Getters and setters
    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 