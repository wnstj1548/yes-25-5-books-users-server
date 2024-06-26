package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class PointException extends ApplicationException{
    public PointException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
