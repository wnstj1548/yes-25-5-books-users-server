package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class AddressNotFoundException extends ApplicationException{
    public AddressNotFoundException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}