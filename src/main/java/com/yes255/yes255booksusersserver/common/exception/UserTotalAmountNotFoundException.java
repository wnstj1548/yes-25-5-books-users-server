package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class UserTotalAmountNotFoundException extends ApplicationException{
    public UserTotalAmountNotFoundException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
