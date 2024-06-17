package com.yes255.yes255booksusersserver.persistance.exception;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException() {
        super("book not found");
    }
}
