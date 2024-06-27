package com.yes255.yes255booksusersserver.presentation.dto.response;

import com.yes255.yes255booksusersserver.persistance.domain.BookAuthor;
import lombok.Builder;

@Builder
public record BookAuthorResponse(
        Long bookAuthorId,
        Long bookId,
        Long AuthorId
)
{
    public static BookAuthorResponse fromEntity(BookAuthor bookAuthor) {
        return BookAuthorResponse.builder()
                .bookAuthorId(bookAuthor.getBookAuthorId())
                .bookId(bookAuthor.getBook().getBookId())
                .AuthorId(bookAuthor.getAuthor().getAuthorId())
                .build();
    }
}
