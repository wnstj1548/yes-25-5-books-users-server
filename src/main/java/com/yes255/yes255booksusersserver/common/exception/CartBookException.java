package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class CartBookException extends ApplicationException{
    public CartBookException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
