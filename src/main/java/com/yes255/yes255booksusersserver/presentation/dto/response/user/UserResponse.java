package com.yes255.yes255booksusersserver.presentation.dto.response.user;

import com.yes255.yes255booksusersserver.persistance.domain.User;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record UserResponse(Long userId, String userName, String userPhone, String userEmail, LocalDate userBirth,
                           LocalDateTime userRegisterDate, LocalDateTime userLastLoginDate,
                           Long providerId, Long userStateId, Long userGradeId, String userPassword) {

    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .userPhone(user.getUserPhone())
                .userEmail(user.getUserEmail())
                .userBirth(user.getUserBirth())
                .userRegisterDate(user.getUserRegisterDate())
                .userLastLoginDate(user.getUserLastLoginDate())
                .providerId(user.getProvider().getProviderId())
                .userStateId(user.getUserState().getUserStateId())
                .userGradeId(user.getUserGrade().getUserGradeId())
                .userPassword(user.getUserPassword())
                .build();
    }
}
