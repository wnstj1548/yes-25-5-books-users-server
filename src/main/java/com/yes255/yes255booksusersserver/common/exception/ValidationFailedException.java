package com.yes255.yes255booksusersserver.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;

import java.util.stream.Collectors;

@Slf4j
public class ValidationFailedException extends RuntimeException {
    public ValidationFailedException(BindingResult bindingResult) {
        super(bindingResult.getAllErrors()
                .stream()
                .map(error -> new StringBuilder()
                        .append(error.getDefaultMessage()))
                .collect(Collectors.joining(" | ")));
    }
}
