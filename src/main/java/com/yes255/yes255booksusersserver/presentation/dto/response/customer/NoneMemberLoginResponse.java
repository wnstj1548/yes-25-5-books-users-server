package com.yes255.yes255booksusersserver.presentation.dto.response.customer;

public record NoneMemberLoginResponse(String accessToken, String refreshToken, Long customerId, String role) {
}
