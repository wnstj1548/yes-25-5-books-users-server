package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class UserAddressNotFoundException extends ApplicationException {
    public UserAddressNotFoundException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}