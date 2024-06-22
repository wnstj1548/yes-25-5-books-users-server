package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class CartBookAlreadyExistedException extends ApplicationException{
    public CartBookAlreadyExistedException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
