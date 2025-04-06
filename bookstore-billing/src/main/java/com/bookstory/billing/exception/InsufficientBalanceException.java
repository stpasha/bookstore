package com.bookstory.billing.exception;

public class InsufficientBalanceException extends IllegalArgumentException {

    public InsufficientBalanceException(String s) {
        super(s);
    }
}
