package com.yes255.yes255booksusersserver.presentation.dto.request.user;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record FindEmailRequest(String name,
                               @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "유효한 전화번호 형식이 아닙니다. 010-1234-5678 형식을 따라야 합니다.")
                               String phone) {
}
