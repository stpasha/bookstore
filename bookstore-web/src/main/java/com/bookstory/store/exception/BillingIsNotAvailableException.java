package com.bookstory.store.exception;

public class BillingIsNotAvailableException extends RuntimeException {
    public BillingIsNotAvailableException() {
    }

    public BillingIsNotAvailableException(String message) {
        super(message);
    }

    public BillingIsNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public BillingIsNotAvailableException(Throwable cause) {
        super(cause);
    }
}
