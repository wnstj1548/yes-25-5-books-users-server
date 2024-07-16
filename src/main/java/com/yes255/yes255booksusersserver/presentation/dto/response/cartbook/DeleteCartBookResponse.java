package com.yes255.yes255booksusersserver.presentation.dto.response.cartbook;

import lombok.Builder;

@Builder
public record DeleteCartBookResponse(Long bookId) {

    public static DeleteCartBookResponse from(Long bookId) {
        return DeleteCartBookResponse.builder()
            .bookId(bookId)
            .build();
    }
}
