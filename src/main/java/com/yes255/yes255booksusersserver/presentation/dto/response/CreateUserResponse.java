package com.yes255.yes255booksusersserver.presentation.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateUserResponse {
    private String userName;

    private LocalDate userBirth = null;

    private String userEmail;

    private String userPhone;

    private String userPassword;

    private String userConfirmPassword;

    @Builder
    public CreateUserResponse(String userName, LocalDate userBirth, String userEmail,
                             String userPhone, String userPassword, String userConfirmPassword) {

        this.userName = userName;
        this.userBirth = userBirth;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userPassword = userPassword;
        this.userConfirmPassword = userConfirmPassword;
    }
}
