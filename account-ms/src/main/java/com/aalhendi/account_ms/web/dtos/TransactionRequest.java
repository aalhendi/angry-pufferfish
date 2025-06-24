package com.aalhendi.account_ms.web.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

/**
 * Request DTO for account transactions (credit/debit).
 */
public class TransactionRequest {

    @JsonProperty("amount")
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.001", message = "Amount must be positive")
    private BigDecimal amount;

    @JsonProperty("description")
    private String description;

    public TransactionRequest() {
    }

    public TransactionRequest(BigDecimal amount, String description) {
        this.amount = amount;
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
} 