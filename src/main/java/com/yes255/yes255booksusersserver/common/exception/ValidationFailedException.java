package com.yes255.yes255booksusersserver.common.exception;

import org.springframework.validation.BindingResult;

import java.util.stream.Collectors;

public class ValidationFailedException extends RuntimeException {
    public ValidationFailedException(BindingResult bindingResult) {
        super(bindingResult.getAllErrors()
                .stream()
                .map(error -> new StringBuilder()
                        .append(error.getDefaultMessage()))
                .collect(Collectors.joining(" | ")));
    }
}