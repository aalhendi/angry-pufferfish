package com.aalhendi.customer_ms.web.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating an existing customer.
 * All fields are optional for partial updates.
 */
public class UpdateCustomerRequest {

    @JsonProperty("name")
    @Size(max = 255, message = "Customer name cannot exceed 255 characters")
    private String name;

    @JsonProperty("address")
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    @JsonProperty("customer_type")
    @Pattern(regexp = "RETAIL|CORPORATE|INVESTMENT", message = "Customer type must be RETAIL, CORPORATE, or INVESTMENT")
    private String customerType;

    /**
     * Default constructor for JSON deserialization.
     */
    public UpdateCustomerRequest() {
    }

    /**
     * Constructor with all fields.
     */
    public UpdateCustomerRequest(String name, String address, String customerType) {
        this.name = name;
        this.address = address;
        this.customerType = customerType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    /**
     * Checks if the request has any updates.
     */
    public boolean hasUpdates() {
        return name != null || address != null || customerType != null;
    }
} 