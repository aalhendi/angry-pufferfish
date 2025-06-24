package com.aalhendi.account_ms.web.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard error response DTO for all API errors.
 * TODO(aalhendi): should be part of the common lib.
 *
 * @param errorCode a machine-readable error code
 * @param message a human-readable error message
 * @param details additional error details (validation errors, etc.)
 * @param timestamp when the error occurred
 */
public record ErrorResponse(
    @JsonProperty("error_code")
    String errorCode,
    
    @JsonProperty("message")
    String message,
    
    @JsonProperty("details")
    List<String> details,
    
    @JsonProperty("timestamp")
    LocalDateTime timestamp
) {
    
    /**
     * Convenience constructor with current timestamp.
     *
     * @param errorCode the error code
     * @param message the error message
     * @param details the error details
     */
    public ErrorResponse(String errorCode, String message, List<String> details) {
        this(errorCode, message, details, LocalDateTime.now());
    }
    
    /**
     * Convenience constructor for simple errors without details.
     *
     * @param errorCode the error code
     * @param message the error message
     */
    public ErrorResponse(String errorCode, String message) {
        this(errorCode, message, List.of(), LocalDateTime.now());
    }
} 