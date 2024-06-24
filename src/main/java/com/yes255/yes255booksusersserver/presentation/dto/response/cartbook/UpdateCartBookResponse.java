package com.yes255.yes255booksusersserver.presentation.dto.response.cartbook;

import lombok.Builder;

@Builder
public record UpdateCartBookResponse(Long cartBookId, int bookQuantity) {
}
