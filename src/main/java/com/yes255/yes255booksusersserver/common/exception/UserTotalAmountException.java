package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class UserTotalAmountException extends ApplicationException{
    public UserTotalAmountException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
