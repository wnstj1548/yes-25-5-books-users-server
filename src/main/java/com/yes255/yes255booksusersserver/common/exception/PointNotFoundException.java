package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class PointNotFoundException extends ApplicationException{
    public PointNotFoundException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
