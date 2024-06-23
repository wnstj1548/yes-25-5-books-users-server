package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class UserEmailAlreadyExistedException extends ApplicationException{
    public UserEmailAlreadyExistedException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
