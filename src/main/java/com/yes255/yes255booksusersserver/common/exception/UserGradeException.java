package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class UserGradeException extends ApplicationException{
    public UserGradeException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
