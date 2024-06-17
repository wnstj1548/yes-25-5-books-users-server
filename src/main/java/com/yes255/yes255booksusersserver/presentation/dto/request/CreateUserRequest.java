package com.yes255.yes255booksusersserver.presentation.dto.request;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record CreateUserRequest(String userName, LocalDate userBirth, String userEmail,
                                String userPhone, String userPassword, String userConfirmPassword) {
}

