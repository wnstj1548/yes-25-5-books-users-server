package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class UserAddressLimitExceededException extends ApplicationException{
    public UserAddressLimitExceededException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
