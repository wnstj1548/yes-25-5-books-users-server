package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class ProviderException extends ApplicationException{
    public ProviderException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
