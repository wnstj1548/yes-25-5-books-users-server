package com.yes255.yes255booksusersserver.persistance.exception;

public class BookCategoryNotFoundException extends RuntimeException {
    public BookCategoryNotFoundException() {
        super("Book category not found");
    }
}
