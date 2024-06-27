package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class JwtException extends ApplicationException {
    public JwtException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
