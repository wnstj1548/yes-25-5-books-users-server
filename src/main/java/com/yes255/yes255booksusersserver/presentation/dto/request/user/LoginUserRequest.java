package com.yes255.yes255booksusersserver.presentation.dto.request.user;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record LoginUserRequest(@Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "유효한 이메일 형식이 아닙니다. yes255@shop.net 형식을 따라야 합니다.")
                               String email,
                               String password) {
}
