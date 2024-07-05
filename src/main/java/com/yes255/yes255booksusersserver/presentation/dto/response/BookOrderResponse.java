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
        String bookImage
)
{
    public static BookOrderResponse fromEntity(Book book) {
        return BookOrderResponse.builder()
                .bookId(book.getBookId())
                .bookName(book.getBookName())
                .bookPrice(book.getBookPrice())
                .bookIsPackable(book.isBookIsPackable())
                .bookImage(book.getBookImage())
                .build();
    }
}
