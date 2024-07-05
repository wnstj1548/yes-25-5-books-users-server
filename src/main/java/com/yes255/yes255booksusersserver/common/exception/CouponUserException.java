package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class CouponUserException extends ApplicationException {
    public CouponUserException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
