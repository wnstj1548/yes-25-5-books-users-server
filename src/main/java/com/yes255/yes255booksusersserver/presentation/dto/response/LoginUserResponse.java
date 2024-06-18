package com.yes255.yes255booksusersserver.presentation.dto.response;

import lombok.Builder;

@Builder
public record LoginUserResponse(String email, String password, String userRole, String loginStatusName) {
}
