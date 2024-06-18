package com.yes255.yes255booksusersserver.presentation.dto.response;

import lombok.Builder;

@Builder
public record UserStateResponse(Long userStateId, String userStateName) {
}
