package com.yes255.yes255booksusersserver.presentation.dto.request.address;

import lombok.Builder;

@Builder
public record CreateAddressRequest(String addressZip, String addressRaw) {
}
