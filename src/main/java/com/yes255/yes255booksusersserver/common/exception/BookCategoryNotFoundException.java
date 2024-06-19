package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;

public class BookCategoryNotFoundException extends ApplicationException {
    public BookCategoryNotFoundException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
