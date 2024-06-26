package com.yes255.yes255booksusersserver.presentation.dto.request.user;

import lombok.Builder;

@Builder
public record FindPasswordRequest(String email, String name) {
}
