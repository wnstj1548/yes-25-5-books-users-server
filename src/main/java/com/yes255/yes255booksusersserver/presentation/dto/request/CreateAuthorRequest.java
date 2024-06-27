package com.yes255.yes255booksusersserver.presentation.dto.request;

import com.yes255.yes255booksusersserver.persistance.domain.Author;

public record CreateAuthorRequest(
        String authorName
)
{
    public Author toEntity() {
        return Author.builder()
                .authorId(null)
                .authorName(authorName)
                .build();
    }
}
