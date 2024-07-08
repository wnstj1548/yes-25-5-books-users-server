package com.yes255.yes255booksusersserver.presentation.dto.request.user;

import java.time.LocalDate;

import com.yes255.yes255booksusersserver.persistance.domain.*;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record CreateUserRequest(String userName,
                                LocalDate userBirth,
                                String userEmail,
                                @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "유효한 전화번호 형식이 아닙니다. 010-1234-5678 형식을 따라야 합니다.")
                                String userPhone,
                                String userPassword,
                                String userConfirmPassword,
                                String providerName) {   // 페이코 회원 가입을 고려해 providerName 추가
    // 기본값 설정 및 PAYCO 처리
    public CreateUserRequest {
        if (providerName == null || providerName.isBlank()) {
            providerName = "LOCAL";
        }

        if ("PAYCO".equals(providerName)) {
            if (!isValidPaycoId(userEmail)) {
                throw new IllegalArgumentException("PAYCO 식별 ID 형식이 아닙니다.");
            }
            userPassword = userEmail;
            userConfirmPassword = userEmail;
        }
        else {
            if (!isValidEmail(userEmail)) {
                throw new IllegalArgumentException("유효한 이메일 형식이 아닙니다. yes255@shop.net 형식을 따라야 합니다.");
            }
        }
    }

    private static boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return java.util.regex.Pattern.compile(emailRegex).matcher(email).matches();
    }

    private static boolean isValidPaycoId(String id) {
        // PAYCO ID의 구체적인 형식에 맞춰 정규식을 조정할 수 있습니다.
        //String paycoIdRegex = "^[a-z; ]{6,30}$";
        //return java.util.regex.Pattern.compile(paycoIdRegex).matcher(id).matches();
        return true;
    }

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

