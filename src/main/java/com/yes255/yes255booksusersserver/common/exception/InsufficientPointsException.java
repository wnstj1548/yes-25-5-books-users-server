package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class InsufficientPointsException extends ApplicationException{
    public InsufficientPointsException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
