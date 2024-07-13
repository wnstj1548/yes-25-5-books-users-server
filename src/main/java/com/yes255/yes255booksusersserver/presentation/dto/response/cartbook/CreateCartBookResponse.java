package com.yes255.yes255booksusersserver.presentation.dto.response.cartbook;

import lombok.Builder;

@Builder
public record CreateCartBookResponse(String cartId) {

    public static CreateCartBookResponse from(String cartId) {
        return CreateCartBookResponse.builder()
            .cartId(cartId)
            .build();
    }
}
