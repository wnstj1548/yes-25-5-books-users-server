package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class UserGradeLogException extends ApplicationException{
    public UserGradeLogException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
