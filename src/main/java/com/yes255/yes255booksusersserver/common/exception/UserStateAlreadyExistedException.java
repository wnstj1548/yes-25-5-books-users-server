package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class UserStateAlreadyExistedException extends ApplicationException {
    public UserStateAlreadyExistedException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
