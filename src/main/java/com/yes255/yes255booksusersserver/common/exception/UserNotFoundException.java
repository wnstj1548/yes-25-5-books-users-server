package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class UserNotFoundException extends ApplicationException {
    public UserNotFoundException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
