package com.yes255.yes255booksusersserver.presentation.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateUserRequest {

    private String userName;

    private LocalDate userBirth = null;

    private String userEmail;

    private String userPhone;

    private String userPassword;

    private String userConfirmPassword;

    // 생일 필수
    @Builder
    public CreateUserRequest(String userName, LocalDate userBirth, String userEmail,
                             String userPhone, String userPassword, String userConfirmPassword) {

        this.userName = userName;
        this.userBirth = userBirth;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userPassword = userPassword;
        this.userConfirmPassword = userConfirmPassword;
    }

    // 생일 선택
    @Builder
    public CreateUserRequest(String userName, String userEmail,
                             String userPhone, String userPassword, String userConfirmPassword) {

        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userPassword = userPassword;
        this.userConfirmPassword = userConfirmPassword;
    }
}
