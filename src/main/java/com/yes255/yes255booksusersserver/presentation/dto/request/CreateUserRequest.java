package com.yes255.yes255booksusersserver.presentation.dto.request;

import java.time.LocalDate;

import com.yes255.yes255booksusersserver.persistance.domain.*;
import lombok.Builder;

@Builder
public record CreateUserRequest(String userName, LocalDate userBirth, String userEmail,
                                String userPhone, String userPassword, String userConfirmPassword) {

    public User toEntity(Customer customer, Provider provider, UserGrade userGrade, UserState userState) {
        return User.builder()
                .customer(customer)
                .userName(userName)
                .userBirth(userBirth)
                .userEmail(userEmail)
                .userPhone(userPhone)
                .provider(provider)
                .userGrade(userGrade)
                .userState(userState)
                .userPassword(userPassword)
                .build();
    }
}

