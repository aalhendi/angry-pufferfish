package com.aalhendi.account_ms.web.controllers;

import com.aalhendi.account_ms.domain.exceptions.BusinessException;
import com.aalhendi.account_ms.web.dtos.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for the account microservice.
 * Handles all business exceptions and converts them to proper HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles all business exceptions using the AccountError enum.
     * Each business exception knows its own HTTP status and error code.
     *
     * @param ex the business exception
     * @return appropriate HTTP response with error details
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        logger.warn("Business exception occurred: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getErrorCode(),
            ex.getMessage(),
            List.of()
        );
        
        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    /**
     * Handles validation errors from @Valid annotations.
     *
     * @param ex the validation exception
     * @return HTTP 400 with validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        logger.warn("Validation exception occurred: {}", ex.getMessage());
        
        List<String> details = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            details.add("%s: %s".formatted(error.getField(), error.getDefaultMessage()))
        );
        
        ErrorResponse errorResponse = new ErrorResponse(
            "VALIDATION_ERROR",
            "Validation failed",
            details
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handles constraint violation exceptions.
     *
     * @param ex the constraint violation exception
     * @return HTTP 400 with constraint violation details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        logger.warn("Constraint violation occurred: {}", ex.getMessage());
        
        List<String> details = new ArrayList<>();
        ex.getConstraintViolations().forEach(violation -> 
            details.add("%s: %s".formatted(violation.getPropertyPath(), violation.getMessage()))
        );
        
        ErrorResponse errorResponse = new ErrorResponse(
            "VALIDATION_ERROR",
            "Constraint validation failed",
            details
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handles malformed JSON requests.
     *
     * @param ex the message not readable exception
     * @return HTTP 400 with error details
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJsonException(HttpMessageNotReadableException ex) {
        logger.warn("Malformed JSON request: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "MALFORMED_JSON",
            "Request body contains invalid JSON",
            List.of(ex.getMostSpecificCause().getMessage())
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handles all other unexpected exceptions.
     *
     * @param ex the unexpected exception
     * @return HTTP 500 with generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex) {
        logger.error("Unexpected exception occurred", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred",
            List.of()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
} 