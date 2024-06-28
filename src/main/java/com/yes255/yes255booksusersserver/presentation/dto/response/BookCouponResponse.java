package com.yes255.yes255booksusersserver.presentation.dto.response;

import lombok.Builder;

@Builder
public record BookCouponResponse(
        Long bookId,
        String bookName,
        String authorName,
        String bookPublisher
) {
}
