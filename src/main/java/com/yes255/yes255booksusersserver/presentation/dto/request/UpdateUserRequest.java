package com.yes255.yes255booksusersserver.presentation.dto.request;

import com.yes255.yes255booksusersserver.persistance.domain.*;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record UpdateUserRequest(Long userId, String userName, String userPhone, String userEmail, LocalDate userBirth,
                                LocalDateTime userRegisterDate, LocalDateTime userLastLoginDate, Long providerId,
                                Long userGradeId, Long userStateId, String userPassword, String userConfirmPassword) {
}
