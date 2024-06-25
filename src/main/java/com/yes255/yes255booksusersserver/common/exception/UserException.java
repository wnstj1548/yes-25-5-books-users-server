package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class UserException extends ApplicationException {
    public UserException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
