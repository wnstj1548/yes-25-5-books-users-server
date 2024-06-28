package com.yes255.yes255booksusersserver.presentation.dto.response;

import com.yes255.yes255booksusersserver.persistance.domain.BookTag;
import lombok.Builder;

@Builder
public record BookTagResponse (Long bookTagId, Long bookId, Long tagId)
{
    public static BookTagResponse fromEntity(BookTag bookTag) {
        return BookTagResponse.builder()
                .bookTagId(bookTag.getBookTagId())
                .bookId(bookTag.getBook().getBookId())
                .tagId(bookTag.getTag().getTagId())
                .build();
    }
}
