package com.yes255.yes255booksusersserver.common.exception;

import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import java.time.LocalDateTime;

public class EntityNotFoundException extends ApplicationException {

    public EntityNotFoundException(String message) {
        super(ErrorStatus.toErrorStatus(message, 404, LocalDateTime.now()));
    }
}
