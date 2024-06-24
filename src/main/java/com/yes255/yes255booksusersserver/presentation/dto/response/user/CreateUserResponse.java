package com.yes255.yes255booksusersserver.presentation.dto.response.user;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CreateUserResponse(Long UserId, String userName, LocalDate userBirth, String userEmail,
                                 String userPhone, String userPassword, String userConfirmPassword) {
}

