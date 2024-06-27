package com.yes255.yes255booksusersserver.presentation.dto.response;

import com.yes255.yes255booksusersserver.persistance.domain.BookCategory;
import lombok.Builder;

@Builder
public record BookCategoryResponse(Long bookCategoryId, Long bookId, Long categoryId)
{
    public static BookCategoryResponse fromEntity(BookCategory bookCategory) {
        return BookCategoryResponse.builder()
                .bookCategoryId(bookCategory.getBookCategoryId())
                .bookId(bookCategory.getBook().getBookId())
                .categoryId(bookCategory.getCategory().getCategoryId())
                .build();
    }
}
