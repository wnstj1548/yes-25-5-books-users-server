package com.yes255.yes255booksusersserver.presentation.dto.request.cartbook;

import lombok.Builder;

@Builder
public record UpdateCartBookRequest(int quantity) {
}
