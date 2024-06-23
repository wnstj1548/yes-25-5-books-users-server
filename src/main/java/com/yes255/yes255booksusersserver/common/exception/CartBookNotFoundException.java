package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class CartBookNotFoundException extends ApplicationException{
    public CartBookNotFoundException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
