package com.aalhendi.account_ms.domain.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Enum defining all possible account business errors.
 */
public enum AccountError {

    // 404 Not Found errors
    ACCOUNT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "ACCOUNT_NOT_FOUND",
            "Account with number '%s' not found"
    ),

    CUSTOMER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CUSTOMER_NOT_FOUND",
            "Customer with number '%s' not found"
    ),

    // 409 Conflict errors
    ACCOUNT_LIMIT_EXCEEDED(
            HttpStatus.CONFLICT,
            "ACCOUNT_LIMIT_EXCEEDED",
            "Customer '%s' has reached the maximum limit of 10 accounts"
    ),

    SALARY_ACCOUNT_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "SALARY_ACCOUNT_ALREADY_EXISTS",
            "Customer '%s' already has a salary account '%s'"
    ),

    // 400 Bad Request - Insufficient Funds
    INSUFFICIENT_FUNDS(
            HttpStatus.BAD_REQUEST,
            "INSUFFICIENT_FUNDS",
            "Insufficient funds in account '%s'. Available: %s, Required: %s"
    ),

    // 400 Bad Request - Invalid State errors
    ACCOUNT_NOT_ACTIVE(
            HttpStatus.BAD_REQUEST,
            "INVALID_ACCOUNT_STATE",
            "Account '%s' is not active. Current status: %s"
    ),

    INVALID_STATUS_TRANSITION(
            HttpStatus.BAD_REQUEST,
            "INVALID_ACCOUNT_STATE",
            "Account '%s' cannot transition from '%s' to '%s'"
    ),

    ACCOUNT_CLOSED(
            HttpStatus.BAD_REQUEST,
            "INVALID_ACCOUNT_STATE",
            "Account '%s' is closed and cannot be modified"
    ),

    CANNOT_CLOSE_ACCOUNT_WITH_BALANCE(
            HttpStatus.BAD_REQUEST,
            "INVALID_ACCOUNT_STATE",
            "Cannot close account '%s' with remaining balance %s"
    ),

    // 400 Bad Request - Invalid Data errors
    INVALID_ACCOUNT_NUMBER_FORMAT(
            HttpStatus.BAD_REQUEST,
            "INVALID_ACCOUNT_DATA",
            "Invalid account_number '%s': must be exactly 10 digits starting with customer number"
    ),

    INVALID_CUSTOMER_NUMBER_FORMAT(
            HttpStatus.BAD_REQUEST,
            "INVALID_ACCOUNT_DATA",
            "Invalid customer_number '%s': must be exactly 7 digits"
    ),

    INVALID_AMOUNT(
            HttpStatus.BAD_REQUEST,
            "INVALID_ACCOUNT_DATA",
            "Invalid amount '%s': %s"
    ),

    NEGATIVE_AMOUNT(
            HttpStatus.BAD_REQUEST,
            "INVALID_ACCOUNT_DATA",
            "Amount must be positive. Provided: %s"
    ),

    ZERO_AMOUNT(
            HttpStatus.BAD_REQUEST,
            "INVALID_ACCOUNT_DATA",
            "Amount must be greater than zero"
    ),

    INVALID_ACCOUNT_TYPE(
            HttpStatus.BAD_REQUEST,
            "INVALID_ACCOUNT_DATA",
            "Invalid account type '%s': must be one of %s"
    ),

    MISSING_REQUIRED_FIELD(
            HttpStatus.BAD_REQUEST,
            "INVALID_ACCOUNT_DATA",
            "Required field '%s' is missing"
    );

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String messageTemplate;

    AccountError(HttpStatus httpStatus, String errorCode, String messageTemplate) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.messageTemplate = messageTemplate;
    }

    /**
     * Gets the HTTP status code for this error.
     *
     * @return the HTTP status code
     */
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    /**
     * Gets the error code for API responses.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Formats the error message with the provided parameters.
     *
     * @param args the parameters to format into the message
     * @return the formatted error message
     */
    public String formatMessage(Object... args) {
        return messageTemplate.formatted(args);
    }

    /**
     * Gets the raw message template (useful for testing).
     *
     * @return the message template
     */
    public String getMessageTemplate() {
        return messageTemplate;
    }
} 