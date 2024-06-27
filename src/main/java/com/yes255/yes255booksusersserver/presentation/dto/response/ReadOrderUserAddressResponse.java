package com.yes255.yes255booksusersserver.presentation.dto.response;

import lombok.Builder;

@Builder
public record ReadOrderUserAddressResponse(Long userAddressId, String addressRaw, String addressDetail,
                                           String addressName, String zipCode, Boolean addressBased) {
}
