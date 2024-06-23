package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class UserStateNotFoundException extends ApplicationException{
    public UserStateNotFoundException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
