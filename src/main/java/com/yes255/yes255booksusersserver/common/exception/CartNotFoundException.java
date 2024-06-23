package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class CartNotFoundException extends ApplicationException{
    public CartNotFoundException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
