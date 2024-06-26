package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class PointPolicyException extends ApplicationException{
    public PointPolicyException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
