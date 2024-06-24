package com.yes255.yes255booksusersserver.presentation.dto.response.userstate;

import lombok.Builder;

@Builder
public record UserStateResponse(Long userStateId, String userStateName) {
}
