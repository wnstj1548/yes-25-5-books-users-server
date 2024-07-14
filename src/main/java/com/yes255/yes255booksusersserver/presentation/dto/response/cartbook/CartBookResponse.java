package com.yes255.yes255booksusersserver.presentation.dto.response.cartbook;

import com.yes255.yes255booksusersserver.persistance.domain.Book;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CartBookResponse(Long bookId,
                               String bookName,
                               BigDecimal bookPrice,
                               int cartBookQuantity,
                               Boolean bookIsPackable,
                               String bookImage) {

    public static CartBookResponse of(Book book, Integer quantity) {
        return CartBookResponse.builder()
            .bookId(book.getBookId())
            .bookName(book.getBookName())
            .bookPrice(book.getBookPrice())
            .cartBookQuantity(quantity)
            .bookIsPackable(book.isBookIsPackable())
            .bookImage(book.getBookImage())
            .build();
    }
}
