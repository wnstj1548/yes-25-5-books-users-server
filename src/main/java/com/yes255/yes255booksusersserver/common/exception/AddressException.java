package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class AddressException extends ApplicationException{
    public AddressException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
