package com.yes255.yes255booksusersserver.presentation.dto.response;

import com.yes255.yes255booksusersserver.persistance.domain.Customer;
import lombok.Builder;

@Builder
public record ReadOrderUserInfoResponse(Long userId, String name, String email, String phoneNumber,
                                        Integer points, String role) {

    public static ReadOrderUserInfoResponse fromNoneMember(Customer customer) {
        return ReadOrderUserInfoResponse.builder()
            .userId(customer.getUserId())
            .name("")
            .email("")
            .phoneNumber("")
            .points(0)
            .role("NONE_MEMBER")
            .build();
    }
}
