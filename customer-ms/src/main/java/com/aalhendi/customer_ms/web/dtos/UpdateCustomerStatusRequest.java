package com.aalhendi.customer_ms.web.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Request DTO for updating customer status.
 * Supports status transitions like activate, suspend, freeze, close.
 */
public class UpdateCustomerStatusRequest {

    @JsonProperty("status")
    @NotNull(message = "Status is required")
    @Pattern(regexp = "PENDING|ACTIVE|SUSPENDED|FROZEN|CLOSED",
            message = "Status must be PENDING, ACTIVE, SUSPENDED, FROZEN, or CLOSED")
    private String status;

    @JsonProperty("reason")
    private String reason;

    /**
     * Default constructor for JSON deserialization.
     */
    public UpdateCustomerStatusRequest() {
    }

    /**
     * Constructor with all fields.
     */
    public UpdateCustomerStatusRequest(String status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
} 