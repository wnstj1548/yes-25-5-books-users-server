package com.yes255.yes255booksusersserver.presentation.dto.response.useraddress;

import com.yes255.yes255booksusersserver.persistance.domain.Address;
import com.yes255.yes255booksusersserver.persistance.domain.UserAddress;
import lombok.Builder;

@Builder
public record CreateUserAddressResponse(String addressZip, String addressRaw, String addressName, String addressDetail, boolean addressBased) {

    public static CreateUserAddressResponse fromAddress(Address address, UserAddress userAddress) {
        return CreateUserAddressResponse.builder()
                .addressZip(address.getAddressZip())
                .addressRaw(address.getAddressRaw())
                .addressName(userAddress.getAddressName())
                .addressDetail(userAddress.getAddressDetail())
                .addressBased(userAddress.isAddressBased())
                .build();
    }
}
