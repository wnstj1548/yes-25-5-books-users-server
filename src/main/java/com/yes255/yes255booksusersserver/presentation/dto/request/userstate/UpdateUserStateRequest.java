package com.yes255.yes255booksusersserver.presentation.dto.request.userstate;

import lombok.Builder;

@Builder
public record UpdateUserStateRequest(String userStateName) {
}
