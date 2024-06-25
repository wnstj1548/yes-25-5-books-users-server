package com.yes255.yes255booksusersserver.presentation.dto.response.useraddress;

import lombok.Builder;

@Builder
public record UserAddressResponse(Long userAddressId, Long addressId, String addressZip, String addressRaw, String addressName,
                                  String addressDetail, boolean addressBased, Long userId) {
}
