package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class CustomerException extends ApplicationException{
    public CustomerException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
