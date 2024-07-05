package com.yes255.yes255booksusersserver.presentation.dto.request.user;

import java.time.LocalDate;

import com.yes255.yes255booksusersserver.persistance.domain.*;
import lombok.Builder;

@Builder
public record CreateUserRequest(String userName, LocalDate userBirth, String userEmail,
                                String userPhone, String userPassword, String userConfirmPassword, String providerName) {   // 페이코 회원 가입을 고려해 providerName 추가


    public User toEntity(Customer customer, Provider provider, UserState userState, UserGrade userGrade) {
        return User.builder()
                .customer(customer)
                .userName(userName)
                .userBirth(userBirth)
                .userEmail(userEmail)
                .userPhone(userPhone)
                .provider(provider)
                .userState(userState)
                .userGrade(userGrade)
                .userPassword(userPassword)
                .build();
    }
}

