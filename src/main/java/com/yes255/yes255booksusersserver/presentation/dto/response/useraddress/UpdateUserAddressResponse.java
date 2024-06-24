package com.yes255.yes255booksusersserver.presentation.dto.response.useraddress;

import lombok.Builder;

@Builder
public record UpdateUserAddressResponse(String addressName, String addressDetail, boolean addressBased) {
}
