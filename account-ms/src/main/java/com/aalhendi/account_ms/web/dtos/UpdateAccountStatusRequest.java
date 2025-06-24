package com.aalhendi.account_ms.web.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Request DTO for updating account status.
 */
public class UpdateAccountStatusRequest {

    @JsonProperty("status")
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "PENDING|ACTIVE|SUSPENDED|FROZEN|CLOSED",
            message = "Status must be PENDING, ACTIVE, SUSPENDED, FROZEN, or CLOSED")
    private String status;

    @JsonProperty("reason")
    private String reason;

    public UpdateAccountStatusRequest() {
    }

    public UpdateAccountStatusRequest(String status, String reason) {
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