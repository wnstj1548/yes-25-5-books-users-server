package com.yes255.yes255booksusersserver.presentation.dto.request.useraddress;

import lombok.Builder;

@Builder
public record UserAddressResponse(Long userAddressID, Long addressId, String addressName,
                                  String addressDetail, boolean addressBased, Long userId) {
}
