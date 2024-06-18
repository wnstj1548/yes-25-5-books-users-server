package com.yes255.yes255booksusersserver.presentation.dto.request;

import com.yes255.yes255booksusersserver.persistance.domain.Customer;
import lombok.Builder;

@Builder
public record CreateCustomerRequest(String userRole) {

    public Customer toEntity() {
        return Customer.builder()
                .userRole(userRole)
                .build();
    }
}
