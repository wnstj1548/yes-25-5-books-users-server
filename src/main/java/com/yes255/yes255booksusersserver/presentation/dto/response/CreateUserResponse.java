package com.yes255.yes255booksusersserver.presentation.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
public record CreateUserResponse(Long UserId, String userName, LocalDate userBirth, String userEmail,
                                 String userPhone, String userPassword, String userConfirmPassword) {
}

