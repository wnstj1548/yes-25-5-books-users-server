package com.yes255.yes255booksusersserver.presentation.dto.response;

import lombok.Builder;

@Builder
public record CreateCartBookResponse(Long cartBookId, int bookQuantity) {
}
