package com.yes255.yes255booksusersserver.presentation.dto.response;

import com.yes255.yes255booksusersserver.persistance.domain.Book;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BookOrderResponse(
        Long bookId,
        String bookName,
        BigDecimal bookPrice,
        Boolean bookIsPackable,
        String bookImage,
        Integer quantity,
        String author
)
{
}
