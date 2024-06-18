package com.yes255.yes255booksusersserver.presentation.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UpdateUserResponse(String userName, LocalDate userBirth, String userPhone) {
}
