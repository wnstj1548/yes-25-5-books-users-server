package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class UserAddressException extends ApplicationException {
    public UserAddressException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
