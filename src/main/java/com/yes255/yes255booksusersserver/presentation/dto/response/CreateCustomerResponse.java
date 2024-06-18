package com.yes255.yes255booksusersserver.presentation.dto.response;

import lombok.Builder;

@Builder
public record CreateCustomerResponse(Long customerId, String userRole) {
}

