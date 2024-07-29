package com.yes255.yes255booksusersserver.presentation.dto.request.user;

import lombok.Builder;

@Builder
public record CheckPasswordRequest(String password) {
}
