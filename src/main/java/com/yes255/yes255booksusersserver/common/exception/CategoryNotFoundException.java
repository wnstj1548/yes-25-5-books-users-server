package com.yes255.yes255booksusersserver.common.exception;

public class CategoryNotFoundException extends RuntimeException{

    public CategoryNotFoundException() {
        super("category not found");
    }
}
