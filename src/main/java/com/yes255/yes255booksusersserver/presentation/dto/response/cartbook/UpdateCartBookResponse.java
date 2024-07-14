package com.yes255.yes255booksusersserver.presentation.dto.response.cartbook;

import lombok.Builder;

@Builder
public record UpdateCartBookResponse(String cartId, int bookQuantity) {

    public static UpdateCartBookResponse of(String cartId, int quantity) {
        return UpdateCartBookResponse.builder()
            .cartId(cartId)
            .bookQuantity(quantity)
            .build();
    }
}
