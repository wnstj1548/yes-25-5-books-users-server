package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class CartException extends ApplicationException{
    public CartException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
