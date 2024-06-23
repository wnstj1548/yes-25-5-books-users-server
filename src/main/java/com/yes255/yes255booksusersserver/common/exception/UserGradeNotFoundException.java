package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class UserGradeNotFoundException extends ApplicationException{
    public UserGradeNotFoundException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
