package com.yes255.yes255booksusersserver.presentation.dto.response;

import lombok.Builder;

@Builder
public record ReadUserInfoResponse(Long gradeId, Integer points, String name, Long userId) {
}
