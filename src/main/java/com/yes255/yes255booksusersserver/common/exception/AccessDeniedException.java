package com.yes255.yes255booksusersserver.common.exception;


import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import java.time.LocalDateTime;

public class AccessDeniedException extends ApplicationException {

    public AccessDeniedException(String message) {
        super(ErrorStatus.toErrorStatus(message, 403, LocalDateTime.now()));
    }
}
