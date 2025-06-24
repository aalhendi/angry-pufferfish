package com.aalhendi.account_ms.web.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Request DTO for creating a new account.
 */
public class CreateAccountRequest {

    @JsonProperty("customer_number")
    @NotBlank(message = "Customer number is required")
    @Pattern(regexp = "\\d{7}", message = "Customer number must be exactly 7 digits")
    private String customerNumber;

    @JsonProperty("account_type")
    @NotBlank(message = "Account type is required")
    @Pattern(regexp = "SAVING|INVESTMENT|SALARY", message = "Account type must be SAVING, INVESTMENT, or SALARY")
    private String accountType;

    public CreateAccountRequest() {
    }

    public CreateAccountRequest(String customerNumber, String accountType) {
        this.customerNumber = customerNumber;
        this.accountType = accountType;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
} 