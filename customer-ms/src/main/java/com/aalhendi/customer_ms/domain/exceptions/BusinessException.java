package com.aalhendi.customer_ms.domain.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Single business exception class that uses the CustomerError enum.
 */
public final class BusinessException extends RuntimeException {

    private final CustomerError error;
    private final Object[] messageArgs;

    /**
     * Creates a business exception with the specified error and message arguments.
     *
     * @param error       the customer error enum value
     * @param messageArgs arguments to format into the error message
     */
    public BusinessException(CustomerError error, Object... messageArgs) {
        super(error.formatMessage(messageArgs));
        this.error = error;
        this.messageArgs = messageArgs;
    }

    /**
     * Creates a business exception with the specified error, cause, and message arguments.
     *
     * @param error       the customer error enum value
     * @param cause       the underlying cause
     * @param messageArgs arguments to format into the error message
     */
    public BusinessException(CustomerError error, Throwable cause, Object... messageArgs) {
        super(error.formatMessage(messageArgs), cause);
        this.error = error;
        this.messageArgs = messageArgs;
    }

    /**
     * Gets the customer error enum value.
     *
     * @return the customer error
     */
    public CustomerError getError() {
        return error;
    }

    /**
     * Gets the business error code for API responses.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return error.getErrorCode();
    }

    /**
     * Gets the appropriate HTTP status code for this exception.
     *
     * @return the HTTP status code
     */
    public HttpStatus getHttpStatus() {
        return error.getHttpStatus();
    }

    /**
     * Gets the message arguments used to format the error message.
     *
     * @return the message arguments
     */
    public Object[] getMessageArgs() {
        return messageArgs.clone();
    }
} 