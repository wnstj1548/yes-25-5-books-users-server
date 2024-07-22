package com.yes255.yes255booksusersserver.presentation.dto.response.useraddress;

import com.yes255.yes255booksusersserver.persistance.domain.UserAddress;
import lombok.Builder;

@Builder
public record UserAddressResponse(Long userAddressId, Long addressId, String addressZip, String addressRaw, String addressName,
                                  String addressDetail, boolean addressBased, Long userId) {

    public static UserAddressResponse fromUserAddress(UserAddress userAddress, Long userId) {
        return UserAddressResponse.builder()
                .userAddressId(userAddress.getUserAddressId())
                .addressId(userAddress.getAddress().getAddressId())
                .addressZip(userAddress.getAddress().getAddressZip())
                .addressRaw(userAddress.getAddress().getAddressRaw())
                .addressName(userAddress.getAddressName())
                .addressDetail(userAddress.getAddressDetail())
                .addressBased(userAddress.isAddressBased())
                .userId(userId)
                .build();
    }
}
