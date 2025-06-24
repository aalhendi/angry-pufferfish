package com.aalhendi.account_ms.domain.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Business exception class that uses the AccountError enum.
 * TODO(aalhendi): we will probably want to make a more generic exception class that can be used for all business exceptions.
 * and have that in the common lib.
 */
public final class BusinessException extends RuntimeException {

    private final AccountError error;
    private final Object[] messageArgs;

    /**
     * Creates a business exception with the specified error and message arguments.
     *
     * @param error       the account error enum value
     * @param messageArgs arguments to format into the error message
     */
    public BusinessException(AccountError error, Object... messageArgs) {
        super(error.formatMessage(messageArgs));
        this.error = error;
        this.messageArgs = messageArgs;
    }

    /**
     * Creates a business exception with the specified error, cause, and message arguments.
     *
     * @param error       the account error enum value
     * @param cause       the underlying cause
     * @param messageArgs arguments to format into the error message
     */
    public BusinessException(AccountError error, Throwable cause, Object... messageArgs) {
        super(error.formatMessage(messageArgs), cause);
        this.error = error;
        this.messageArgs = messageArgs;
    }

    /**
     * Gets the account error enum value.
     *
     * @return the account error
     */
    public AccountError getError() {
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