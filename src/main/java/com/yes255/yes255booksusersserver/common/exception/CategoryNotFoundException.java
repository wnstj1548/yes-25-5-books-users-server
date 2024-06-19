package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class CategoryNotFoundException extends ApplicationException{

    public CategoryNotFoundException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
