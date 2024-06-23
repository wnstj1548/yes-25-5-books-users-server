package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class UserPasswordMismatchException extends ApplicationException{
    public UserPasswordMismatchException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
