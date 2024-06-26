package com.yes255.yes255booksusersserver.presentation.dto.response.cartbook;

import java.math.BigDecimal;

public record CartBookResponse(Long userId, Long cartBookId, Long bookId, String bookName, BigDecimal bookPrice, int cartBookQuantity) {
}
