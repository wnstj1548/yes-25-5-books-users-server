package com.yes255.yes255booksusersserver.presentation.dto.response;

import lombok.Builder;

@Builder
public record ReadOrderUserInfoResponse(Long userId, String name, String email, String phoneNumber,
                                        Integer points, String role) {
}
