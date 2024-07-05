package com.yes255.yes255booksusersserver.presentation.dto.response.cartbook;

import lombok.Builder;

@Builder
public record CreateCartBookResponse(Long bookId, int quantity) {
}
