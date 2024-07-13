package com.yes255.yes255booksusersserver.presentation.dto.request.cartbook;

import lombok.Builder;

@Builder
public record UpdateCartBookOrderRequest(Long bookId, int quantity, String cartId) {
}
