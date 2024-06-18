package com.yes255.yes255booksusersserver.common.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.validation.BindingResult;

import java.util.Set;
import java.util.stream.Collectors;

//public class ValidationFailedException extends RuntimeException {
//    public ValidationFailedException(BindingResult bindingResult) {
//        super(bindingResult.getAllErrors()
//                .stream()
//                .map(error -> new StringBuilder()
//                        .append(error.getDefaultMessage()))
//                .collect(Collectors.joining(" | ")));
//    }
//}


public class ValidationFailedException extends ConstraintViolationException {

    public ValidationFailedException(String message, Set<? extends ConstraintViolation<?>> constraintViolations) {
        super(message, constraintViolations);
    }
}
