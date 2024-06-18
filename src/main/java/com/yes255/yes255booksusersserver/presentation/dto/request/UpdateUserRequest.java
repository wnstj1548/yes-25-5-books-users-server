package com.yes255.yes255booksusersserver.presentation.dto.request;

import com.yes255.yes255booksusersserver.persistance.domain.*;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record UpdateUserRequest(String userName, String userPhone, LocalDate userBirth,
                                String userPassword, String userConfirmPassword) {
}
