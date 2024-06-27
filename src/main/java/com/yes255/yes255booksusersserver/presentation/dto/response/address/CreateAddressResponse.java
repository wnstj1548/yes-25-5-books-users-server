package com.yes255.yes255booksusersserver.presentation.dto.response.address;

import lombok.Builder;

@Builder
public record CreateAddressResponse(String addressZip, String addressRaw) {
}
