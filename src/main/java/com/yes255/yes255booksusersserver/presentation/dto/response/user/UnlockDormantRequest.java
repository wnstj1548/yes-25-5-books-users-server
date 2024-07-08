package com.yes255.yes255booksusersserver.presentation.dto.response.user;

import lombok.Builder;

@Builder
public record UnlockDormantRequest(String email) {

    public static UnlockDormantRequest from(String email) {
        return UnlockDormantRequest.builder()
            .email(email)
            .build();
    }
}
