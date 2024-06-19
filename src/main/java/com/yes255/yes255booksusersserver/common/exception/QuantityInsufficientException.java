package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class QuantityInsufficientException extends ApplicationException{
    public QuantityInsufficientException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}