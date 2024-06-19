package com.yes255.yes255booksusersserver.presentation.dto.request;

import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.domain.BookTag;
import com.yes255.yes255booksusersserver.persistance.domain.Tag;
import jakarta.validation.constraints.NotNull;

public record CreateBookTagRequest(

        @NotNull(message = "책은 필수 입력 항목입니다.")
        Book book,

        @NotNull(message = "태그는 필수 입력 항목입니다.")
        Tag tag
) {
    public BookTag toEntity() {
        return BookTag.builder()
                .book(book)
                .tag(tag)
                .build();
    }
}