package com.yes255.yes255booksusersserver.presentation.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record UserResponse(Long userId, String userName, String userPhone, String userEmail, LocalDate userBirth,
                           LocalDateTime userRegisterDate, LocalDateTime userLastLoginDate,
                           Long providerId, Long userGradeId, Long userStateId, String userPassword) {
}
