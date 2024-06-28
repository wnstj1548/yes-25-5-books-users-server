package com.yes255.yes255booksusersserver.presentation.dto.response;

import com.yes255.yes255booksusersserver.persistance.domain.Book;
import lombok.Builder;

@Builder
public record BookOrderResponse(
        String bookName
)
{
    public static BookOrderResponse fromEntity(Book book) {
        return BookOrderResponse.builder()
                .bookName(book.getBookName())
                .build();
    }
}
