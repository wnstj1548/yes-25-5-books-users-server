package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class CustmorNotfoundException extends ApplicationException{
    public CustmorNotfoundException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
