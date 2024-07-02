package com.yes255.yes255booksusersserver.presentation.dto.response.customer;

import lombok.Builder;

@Builder
public record NonMemberResponse(Long userId, String userRole, Long cartId) {
}
