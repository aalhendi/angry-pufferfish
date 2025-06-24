package com.aalhendi.customer_ms.web.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a new customer.
 * Contains customer information required for registration.
 */
public class CreateCustomerRequest {

    @JsonProperty("name")
    @NotBlank(message = "Customer name is required")
    @Size(max = 255, message = "Customer name cannot exceed 255 characters")
    private String name;

    @JsonProperty("national_id")
    @NotBlank(message = "National ID is required")
    @Pattern(regexp = "\\d{12}", message = "National ID must be exactly 12 digits")
    private String nationalId;

    @JsonProperty("customer_type")
    @NotNull(message = "Customer type is required")
    @Pattern(regexp = "RETAIL|CORPORATE|INVESTMENT", message = "Customer type must be RETAIL, CORPORATE, or INVESTMENT")
    private String customerType;

    @JsonProperty("address")
    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    /**
     * Default constructor for JSON deserialization.
     */
    public CreateCustomerRequest() {
    }

    /**
     * Constructor with all fields.
     */
    public CreateCustomerRequest(String name, String nationalId, String customerType, String address) {
        this.name = name;
        this.nationalId = nationalId;
        this.customerType = customerType;
        this.address = address;
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
} 