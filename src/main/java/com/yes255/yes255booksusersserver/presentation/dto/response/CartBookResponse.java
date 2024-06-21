package com.yes255.yes255booksusersserver.presentation.dto.response;

import com.yes255.yes255booksusersserver.persistance.domain.Book;

import java.math.BigDecimal;

public record CartBookResponse(Long cartBookId, String bookName, BigDecimal bookPrice, int cartBookQuantity) {
}
