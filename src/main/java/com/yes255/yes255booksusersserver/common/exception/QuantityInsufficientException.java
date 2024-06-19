package com.yes255.yes255booksusersserver.common.exception;

public class QuantityInsufficientException extends RuntimeException{
    public QuantityInsufficientException() {
        super("Quantity insufficient");
    }
}