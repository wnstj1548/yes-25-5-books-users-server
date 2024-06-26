package com.yes255.yes255booksusersserver.presentation.dto.request.user;

import lombok.Builder;

@Builder
public record UpdatePasswordRequest(String password, String confirmPassword) {
}
