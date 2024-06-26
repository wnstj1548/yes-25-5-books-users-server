package com.yes255.yes255booksusersserver.presentation.dto.request.useraddress;

import lombok.Builder;

@Builder
public record UpdateUserAddressRequest(String addressZip, String addressRaw, String addressName, String addressDetail, boolean addressBased) {
}
