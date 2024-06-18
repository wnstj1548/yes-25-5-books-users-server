package com.yes255.yes255booksusersserver.common.exception;

public class BookCategoryNotFoundException extends RuntimeException {
    public BookCategoryNotFoundException() {
        super("Book category not found");
    }
}
