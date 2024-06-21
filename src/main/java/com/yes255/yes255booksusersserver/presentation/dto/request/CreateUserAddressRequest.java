package com.yes255.yes255booksusersserver.presentation.dto.request;

import lombok.Builder;

@Builder
public record CreateUserAddressRequest(String addressName, String addressDetail, boolean addressBased) {
}
