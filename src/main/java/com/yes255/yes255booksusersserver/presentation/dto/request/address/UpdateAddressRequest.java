package com.yes255.yes255booksusersserver.presentation.dto.request.address;

import lombok.Builder;

@Builder
public record UpdateAddressRequest(String addressZip, String addressRaw) {
}
