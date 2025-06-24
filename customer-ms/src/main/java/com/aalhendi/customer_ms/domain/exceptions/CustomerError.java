package com.aalhendi.customer_ms.domain.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Enum defining all possible customer business errors.
 */
public enum CustomerError {

    // 409 Conflict errors
    CUSTOMER_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "CUSTOMER_ALREADY_EXISTS",
            "Customer with national ID '%s' already exists"
    ),

    CUSTOMER_NUMBER_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "CUSTOMER_NUMBER_ALREADY_EXISTS",
            "Customer with number '%s' already exists"
    ),

    // 404 Not Found errors
    CUSTOMER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CUSTOMER_NOT_FOUND",
            "Customer with number '%s' not found"
    ),

    CUSTOMER_NOT_FOUND_BY_NATIONAL_ID(
            HttpStatus.NOT_FOUND,
            "CUSTOMER_NOT_FOUND",
            "Customer with national ID '%s' not found"
    ),

    // 400 Bad Request - Invalid State errors
    CUSTOMER_DELETED(
            HttpStatus.BAD_REQUEST,
            "INVALID_CUSTOMER_STATE",
            "Customer '%s' has been deleted and cannot be modified"
    ),

    INVALID_STATUS_TRANSITION(
            HttpStatus.BAD_REQUEST,
            "INVALID_CUSTOMER_STATE",
            "Customer '%s' cannot transition from '%s' to '%s'"
    ),

    CANNOT_PERFORM_OPERATION(
            HttpStatus.BAD_REQUEST,
            "INVALID_CUSTOMER_STATE",
            "Cannot perform '%s' on customer '%s' in state '%s'"
    ),

    // 400 Bad Request - Invalid Data errors
    INVALID_NATIONAL_ID_FORMAT(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            "National ID must be exactly 12 digits"
    ),

    INVALID_CUSTOMER_NUMBER_FORMAT(
            HttpStatus.BAD_REQUEST,
            "INVALID_CUSTOMER_DATA",
            "Invalid customer_number '%s': must be exactly 7 digits"
    ),

    INVALID_CUSTOMER_NAME(
            HttpStatus.BAD_REQUEST,
            "INVALID_CUSTOMER_DATA",
            "Invalid customer name '%s': %s"
    ),

    INVALID_ADDRESS(
            HttpStatus.BAD_REQUEST,
            "INVALID_CUSTOMER_DATA",
            "Invalid address '%s': %s"
    ),

    MISSING_REQUIRED_FIELD(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            "Required field '%s' is missing"
    ),

    NO_UPDATE_FIELDS_PROVIDED(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            "At least one field must be provided for update"
    ),

    INVALID_CUSTOMER_TYPE(
            HttpStatus.BAD_REQUEST,
            "INVALID_CUSTOMER_DATA",
            "Customer type must be RETAIL, CORPORATE, or INVESTMENT"
    ),

    VALIDATION_ERROR(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            "Invalid value '%s' for field '%s'"
    );

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String messageTemplate;

    CustomerError(HttpStatus httpStatus, String errorCode, String messageTemplate) {
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