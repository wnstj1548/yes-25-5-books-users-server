package com.yes255.yes255booksusersserver.presentation.dto.request;

import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.domain.BookTag;
import com.yes255.yes255booksusersserver.persistance.domain.Tag;

public record CreateBookTagRequest(Book book, Tag tag) {
    public BookTag toEntity() {
        return BookTag.builder()
                .book(book)
                .tag(tag)
                .build();
    }
}
