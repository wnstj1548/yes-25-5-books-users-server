package com.yes255.yes255booksusersserver.presentation.dto.response.user;

import lombok.Builder;

@Builder
public record FindUserResponse(String userEmail) {
}
