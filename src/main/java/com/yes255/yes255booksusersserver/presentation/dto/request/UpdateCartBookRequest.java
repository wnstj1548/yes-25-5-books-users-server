package com.yes255.yes255booksusersserver.presentation.dto.request;

import lombok.Builder;

@Builder
public record UpdateCartBookRequest(Long bookId, int bookQuantity) {
}
