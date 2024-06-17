package com.yes255.yes255booksusersserver.presentation.dto.request;

import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.domain.BookCategory;
import com.yes255.yes255booksusersserver.persistance.domain.Category;

public record UpdateBookCategoryRequest(Long bookCategoryId, Book book, Category category) {

    public BookCategory toEntity() {
        return BookCategory.builder()
                .bookCategoryId(bookCategoryId)
                .book(book)
                .category(category)
                .build();
    }
}
