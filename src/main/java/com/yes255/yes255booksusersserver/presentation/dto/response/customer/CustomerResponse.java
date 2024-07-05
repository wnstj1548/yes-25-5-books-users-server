package com.yes255.yes255booksusersserver.presentation.dto.response.customer;

import lombok.Builder;

@Builder
public record CustomerResponse(Long customerId, String role) {
}
