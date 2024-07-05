package com.yes255.yes255booksusersserver.presentation.dto.response.user;

public record JwtAuthResponse(Long customerId,
                              String role,
                              String loginStateName,
                              String refreshJwt) {

}
