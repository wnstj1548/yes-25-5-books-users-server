package com.yes255.yes255booksusersserver.presentation.dto.response;

import lombok.Builder;

@Builder
public record LoginUserResponse(Long userId, String userRole, String loginStatusName) {
}
