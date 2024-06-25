package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class UserStateException extends ApplicationException{
    public UserStateException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
