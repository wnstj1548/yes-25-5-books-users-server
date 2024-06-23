package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class PointPolicyNotFoundException extends ApplicationException{
    public PointPolicyNotFoundException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
