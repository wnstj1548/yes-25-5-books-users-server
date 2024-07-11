package com.yes255.yes255booksusersserver.presentation.dto.request.useraddress;

import lombok.Builder;

@Builder
public record UpdateAddressBasedRequest(boolean addressBased) {
}
